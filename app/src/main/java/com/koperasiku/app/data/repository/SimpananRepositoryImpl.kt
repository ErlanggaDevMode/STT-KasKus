package com.koperasiku.app.data.repository

import com.koperasiku.app.core.network.NetworkMonitor
import com.koperasiku.app.core.session.SessionManager
import com.koperasiku.app.core.utils.Resource
import com.koperasiku.app.data.local.dao.AnggotaDao
import com.koperasiku.app.data.local.dao.KasDao
import com.koperasiku.app.data.local.dao.MutasiSimpananDao
import com.koperasiku.app.data.local.dao.SimpananDao
import com.koperasiku.app.data.local.entity.KasEntity
import com.koperasiku.app.data.local.entity.MutasiSimpananEntity
import com.koperasiku.app.data.local.entity.SimpananEntity
import com.koperasiku.app.data.mapper.toDomain
import com.koperasiku.app.data.mapper.toEntity
import com.koperasiku.app.data.mapper.toDto
import com.koperasiku.app.data.remote.source.SimpananRemoteSource
import com.koperasiku.app.domain.model.MutasiSimpanan
import com.koperasiku.app.domain.model.Simpanan
import com.koperasiku.app.domain.repository.SimpananRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SimpananRepositoryImpl @Inject constructor(
    private val simpananDao: SimpananDao,
    private val mutasiDao: MutasiSimpananDao,
    private val anggotaDao: AnggotaDao,
    private val kasDao: KasDao,
    private val remoteSource: SimpananRemoteSource,
    private val networkMonitor: NetworkMonitor,
    private val sessionManager: SessionManager
) : SimpananRepository {

    override fun getSimpananList(): Flow<List<Simpanan>> {
        return simpananDao.getAll()
            .map { entities ->
                entities.map { entity ->
                    val anggota = anggotaDao.getById(entity.anggotaId)
                    entity.toDomain(anggotaNama = anggota?.nama)
                }
            }
            .onStart {
                if (networkMonitor.isOnline()) {
                    try {
                        val remoteList = remoteSource.getSimpananList()
                        simpananDao.upsertAll(remoteList.map { it.toDomain().toEntity(isSynced = true) })
                    } catch (e: Exception) {
                        // Gagal sync non-blocking
                    }
                }
            }
            .flowOn(Dispatchers.IO)
    }

    override fun getSimpananForAnggota(anggotaId: String): Flow<List<Simpanan>> {
        return simpananDao.getSimpananForAnggota(anggotaId)
            .map { entities ->
                entities.map { entity ->
                    val anggota = anggotaDao.getById(entity.anggotaId)
                    entity.toDomain(anggotaNama = anggota?.nama)
                }
            }
            .flowOn(Dispatchers.IO)
    }

    override fun getMutasiSimpanan(anggotaId: String): Flow<List<MutasiSimpanan>> {
        return mutasiDao.getByAnggotaId(anggotaId)
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(Dispatchers.IO)
    }

    override fun executeSimpananTransaction(
        anggotaId: String,
        jenisTransaction: String,
        jenisSimpanan: String,
        jumlah: Long,
        keterangan: String?
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            val isOnline = networkMonitor.isOnline()
            val anggota = anggotaDao.getById(anggotaId)
                ?: throw Exception("Anggota tidak terdaftar")

            // 1. Ambil/buat baris Simpanan
            var existingEntity = simpananDao.getSimpananByJenis(anggotaId, jenisSimpanan)
            val currentSaldo = existingEntity?.saldo ?: 0L
            val simpananId = existingEntity?.id ?: UUID.randomUUID().toString()

            val newSaldo = if (jenisTransaction == "SETORAN") {
                currentSaldo + jumlah
            } else {
                if (currentSaldo < jumlah) throw Exception("Saldo $jenisSimpanan tidak mencukupi untuk melakukan penarikan.")
                currentSaldo - jumlah
            }

            val updatedSimpanan = Simpanan(
                id = simpananId,
                anggotaId = anggotaId,
                jenis = jenisSimpanan,
                saldo = newSaldo
            )
            simpananDao.upsert(updatedSimpanan.toEntity(isSynced = isOnline))

            // 2. Tulis Mutasi Simpanan
            val systemTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            val dateNow = systemTime.date.toString()

            val mutasi = MutasiSimpanan(
                id = UUID.randomUUID().toString(),
                anggotaId = anggotaId,
                jenis = jenisTransaction,
                jenisSimpanan = jenisSimpanan,
                jumlah = jumlah,
                saldoSebelum = currentSaldo,
                saldoSesudah = newSaldo,
                keterangan = keterangan ?: "$jenisTransaction Simpanan $jenisSimpanan",
                userId = sessionManager.currentUserId,
                createdAt = Clock.System.now().toString()
            )
            mutasiDao.upsert(mutasi.toEntity())

            // 3. Tulis Ledger Kas Keuangan Koperasi
            val kasEntry = KasEntity(
                id = UUID.randomUUID().toString(),
                jenis = if (jenisTransaction == "SETORAN") "MASUK" else "KELUAR",
                kategoriId = "SIMPANAN",
                jumlah = jumlah,
                keterangan = "${if (jenisTransaction == "SETORAN") "Setoran" else "Penarikan"} Simpanan $jenisSimpanan - ${anggota.nama}",
                referensiId = mutasi.id,
                referensiTipe = "SIMPANAN",
                userId = sessionManager.currentUserId,
                tanggal = dateNow,
                createdAt = Clock.System.now().toString(),
                isSynced = isOnline
            )
            kasDao.upsert(kasEntry)

            // 4. Sync Remote
            if (isOnline) {
                try {
                    remoteSource.upsertSimpanan(updatedSimpanan.toDto())
                    remoteSource.createMutasiSimpanan(mutasi.toDto())
                } catch (e: Exception) {
                    simpananDao.upsert(updatedSimpanan.toEntity(isSynced = false))
                }
            }

            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Gagal memproses transaksi simpanan."))
        }
    }.flowOn(Dispatchers.IO)
}
