package com.koperasiku.app.data.repository

import com.koperasiku.app.core.network.NetworkMonitor
import com.koperasiku.app.core.session.SessionManager
import com.koperasiku.app.core.utils.Resource
import com.koperasiku.app.data.local.dao.AnggotaDao
import com.koperasiku.app.data.local.dao.AngsuranDao
import com.koperasiku.app.data.local.dao.KasDao
import com.koperasiku.app.data.local.dao.PinjamanDao
import com.koperasiku.app.data.local.entity.AngsuranEntity
import com.koperasiku.app.data.local.entity.KasEntity
import com.koperasiku.app.data.mapper.toDomain
import com.koperasiku.app.data.mapper.toDto
import com.koperasiku.app.data.mapper.toEntity
import com.koperasiku.app.data.remote.source.PinjamanRemoteSource
import com.koperasiku.app.domain.model.Angsuran
import com.koperasiku.app.domain.model.Pinjaman
import com.koperasiku.app.domain.repository.PinjamanRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PinjamanRepositoryImpl @Inject constructor(
    private val pinjamanDao: PinjamanDao,
    private val angsuranDao: AngsuranDao,
    private val anggotaDao: AnggotaDao,
    private val kasDao: KasDao,
    private val remoteSource: PinjamanRemoteSource,
    private val networkMonitor: NetworkMonitor,
    private val sessionManager: SessionManager
) : PinjamanRepository {

    override fun getPinjamanList(): Flow<List<Pinjaman>> {
        return pinjamanDao.getAll()
            .map { entities ->
                entities.map { entity ->
                    val anggota = anggotaDao.getById(entity.anggotaId)
                    entity.toDomain(anggotaNama = anggota?.nama)
                }
            }
            .onStart {
                if (networkMonitor.isOnline()) {
                    try {
                        val remoteList = remoteSource.getPinjamanList()
                        pinjamanDao.upsertAll(remoteList.map { it.toDomain().toEntity(isSynced = true) })
                    } catch (e: Exception) { /* non-blocking */ }
                }
            }
            .flowOn(Dispatchers.IO)
    }

    override fun getPinjamanDetail(pinjamanId: String): Flow<Pinjaman?> {
        return pinjamanDao.getById(pinjamanId)
            .map { entity ->
                entity?.let {
                    val anggota = anggotaDao.getById(it.anggotaId)
                    it.toDomain(anggotaNama = anggota?.nama)
                }
            }
            .flowOn(Dispatchers.IO)
    }

    override fun getAngsuranList(pinjamanId: String): Flow<List<Angsuran>> {
        return angsuranDao.getByPinjamanId(pinjamanId)
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(Dispatchers.IO)
    }

    override fun applyPinjaman(pinjaman: Pinjaman): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            val isOnline = networkMonitor.isOnline()
            pinjamanDao.upsert(pinjaman.toEntity(isSynced = isOnline))

            if (isOnline) {
                remoteSource.insertPinjaman(pinjaman.toDto())
            }
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Gagal mengajukan pinjaman."))
        }
    }.flowOn(Dispatchers.IO)

    override fun approvePinjaman(pinjamanId: String, approverId: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            val isOnline = networkMonitor.isOnline()
            val entity = pinjamanDao.getByIdOnce(pinjamanId)
                ?: throw Exception("Data pinjaman tidak ditemukan.")
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            val approvedDate = now.date.toString()

            // Update status → AKTIF
            val updated = entity.copy(
                status = "AKTIF",
                tanggalDisetujui = approvedDate,
                approvedByUserId = approverId,
                isSynced = isOnline
            )
            pinjamanDao.upsert(updated)

            // Generate installment schedule
            val angsuranPokok = entity.jumlahPinjaman / entity.tenorBulan
            val bungaBulanan = (entity.jumlahPinjaman * entity.bungaPersenPerBulan / 100).toLong()
            val angsuranList = mutableListOf<AngsuranEntity>()
            for (i in 1..entity.tenorBulan) {
                val jatuhTempo = now.date.plus(DatePeriod(months = i)).toString()
                angsuranList.add(
                    AngsuranEntity(
                        id = UUID.randomUUID().toString(),
                        pinjamanId = pinjamanId,
                        anggotaId = entity.anggotaId,
                        ke = i,
                        jumlahPokok = angsuranPokok,
                        jumlahBunga = bungaBulanan,
                        jumlahDenda = 0L,
                        jumlahTotal = angsuranPokok + bungaBulanan,
                        status = "BELUM_BAYAR",
                        tanggalJatuhTempo = jatuhTempo,
                        tanggalBayar = null,
                        createdAt = Clock.System.now().toString()
                    )
                )
            }
            angsuranDao.insertAll(angsuranList)

            // Record cash outflow (pencairan pinjaman)
            val anggota = anggotaDao.getById(entity.anggotaId)
            val kasKeluar = KasEntity(
                id = UUID.randomUUID().toString(),
                jenis = "KELUAR",
                kategoriId = "PINJAMAN",
                jumlah = entity.jumlahPinjaman,
                keterangan = "Pencairan Pinjaman No.${entity.nomorPinjaman} - ${anggota?.nama ?: entity.anggotaId}",
                referensiId = pinjamanId,
                referensiTipe = "PINJAMAN",
                userId = approverId,
                tanggal = approvedDate,
                createdAt = Clock.System.now().toString(),
                isSynced = isOnline
            )
            kasDao.upsert(kasKeluar)

            if (isOnline) {
                remoteSource.upsertPinjaman(updated.toDomain().toDto())
                remoteSource.insertAngsuranBatch(angsuranList.map { it.toDomain().toDto() })
            }
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Gagal menyetujui pinjaman."))
        }
    }.flowOn(Dispatchers.IO)

    override fun rejectPinjaman(pinjamanId: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            val isOnline = networkMonitor.isOnline()
            val entity = pinjamanDao.getByIdOnce(pinjamanId)
                ?: throw Exception("Data pinjaman tidak ditemukan.")
            val updated = entity.copy(status = "DITOLAK", isSynced = isOnline)
            pinjamanDao.upsert(updated)

            if (isOnline) {
                remoteSource.upsertPinjaman(updated.toDomain().toDto())
            }
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Gagal menolak pinjaman."))
        }
    }.flowOn(Dispatchers.IO)

    override fun payAngsuran(angsuranId: String, jumlahDenda: Long): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            val isOnline = networkMonitor.isOnline()
            val angsuran = angsuranDao.getByIdOnce(angsuranId)
                ?: throw Exception("Data angsuran tidak ditemukan.")
            val pinjaman = pinjamanDao.getByIdOnce(angsuran.pinjamanId)
                ?: throw Exception("Pinjaman terkait tidak ditemukan.")
            val anggota = anggotaDao.getById(angsuran.anggotaId)
            val now = Clock.System.now()
            val nowStr = now.toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()

            // Update angsuran status → LUNAS
            val totalBayar = angsuran.jumlahTotal + jumlahDenda
            val updatedAngsuran = angsuran.copy(
                status = "LUNAS",
                jumlahDenda = jumlahDenda,
                jumlahTotal = totalBayar,
                tanggalBayar = nowStr
            )
            angsuranDao.upsert(updatedAngsuran)

            // Check if all installments are paid → close loan
            val remaining = angsuranDao.countBelumBayar(angsuran.pinjamanId)
            if (remaining == 0) {
                val updatedPinjaman = pinjaman.copy(status = "LUNAS", isSynced = isOnline)
                pinjamanDao.upsert(updatedPinjaman)
                if (isOnline) remoteSource.upsertPinjaman(updatedPinjaman.toDomain().toDto())
            }

            // Record kas masuk (pembayaran angsuran)
            val kasEntry = KasEntity(
                id = UUID.randomUUID().toString(),
                jenis = "MASUK",
                kategoriId = "ANGSURAN",
                jumlah = totalBayar,
                keterangan = "Bayar Angsuran Ke-${angsuran.ke} No.${pinjaman.nomorPinjaman} - ${anggota?.nama ?: angsuran.anggotaId}",
                referensiId = angsuranId,
                referensiTipe = "ANGSURAN",
                userId = sessionManager.currentUserId,
                tanggal = nowStr,
                createdAt = now.toString(),
                isSynced = isOnline
            )
            kasDao.upsert(kasEntry)

            if (isOnline) {
                remoteSource.upsertAngsuran(updatedAngsuran.toDomain().toDto())
            }
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Gagal memproses pembayaran angsuran."))
        }
    }.flowOn(Dispatchers.IO)
}
