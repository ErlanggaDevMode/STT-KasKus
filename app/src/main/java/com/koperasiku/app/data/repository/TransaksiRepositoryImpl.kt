package com.koperasiku.app.data.repository

import com.koperasiku.app.core.network.NetworkMonitor
import com.koperasiku.app.core.utils.Resource
import com.koperasiku.app.data.local.dao.KasDao
import com.koperasiku.app.data.local.dao.MutasiStokDao
import com.koperasiku.app.data.local.dao.ProdukDao
import com.koperasiku.app.data.local.dao.TransaksiDao
import com.koperasiku.app.data.local.entity.KasEntity
import com.koperasiku.app.data.local.entity.MutasiStokEntity
import com.koperasiku.app.data.mapper.toDomain
import com.koperasiku.app.data.mapper.toEntity
import com.koperasiku.app.data.mapper.toDto
import com.koperasiku.app.data.remote.source.ProdukRemoteSource
import com.koperasiku.app.data.remote.source.TransaksiRemoteSource
import com.koperasiku.app.domain.model.Transaksi
import com.koperasiku.app.domain.repository.TransaksiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransaksiRepositoryImpl @Inject constructor(
    private val transaksiDao: TransaksiDao,
    private val produkDao: ProdukDao,
    private val mutasiDao: MutasiStokDao,
    private val kasDao: KasDao,
    private val remoteSource: TransaksiRemoteSource,
    private val produkRemoteSource: ProdukRemoteSource,
    private val networkMonitor: NetworkMonitor
) : TransaksiRepository {

    override fun getTransaksiHistory(): Flow<List<Transaksi>> {
        return transaksiDao.getAll()
            .map { entities ->
                entities.map { it.toDomain(emptyList()) }
            }
            .flowOn(Dispatchers.IO)
    }

    override fun getTransaksiDetail(id: String): Flow<Resource<Transaksi>> = flow {
        emit(Resource.Loading)
        val header = transaksiDao.getById(id)
        if (header != null) {
            val items = transaksiDao.getItemsForTransaction(id).map { it.toDomain() }
            emit(Resource.Success(header.toDomain(items)))
        } else {
            emit(Resource.Error("Transaksi tidak ditemukan"))
        }
    }.flowOn(Dispatchers.IO)

    override fun processCheckout(transaksi: Transaksi): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            val isOnline = networkMonitor.isOnline()

            // 1. Simpan header transaksi di Room
            transaksiDao.upsert(transaksi.toEntity(isSynced = isOnline))

            // 2. Simpan item-item transaksi, update stok, dan catat mutasi stok
            val itemEntities = transaksi.items.map { item ->
                val product = produkDao.getById(item.produkId)
                val namaProduk = product?.nama ?: "Produk Tidak Dikenal"

                if (product != null) {
                    val newStock = product.stokSaatIni - item.kuantitas
                    produkDao.updateStok(item.produkId, newStock)

                    val mutasi = MutasiStokEntity(
                        id = UUID.randomUUID().toString(),
                        produkId = item.produkId,
                        jenis = "KELUAR",
                        jumlah = -item.kuantitas,
                        stokSebelum = product.stokSaatIni,
                        stokSesudah = newStock,
                        referensiId = transaksi.id,
                        referensiTipe = "TRANSAKSI",
                        keterangan = "Penjualan POS No. ${transaksi.nomorTransaksi}",
                        userId = transaksi.userId,
                        createdAt = Clock.System.now().toString()
                    )
                    mutasiDao.upsert(mutasi)

                    if (isOnline) {
                        try {
                            produkRemoteSource.updateStok(item.produkId, newStock)
                        } catch (e: Exception) {
                            // Gagal sync remote non-blocking
                        }
                    }
                }

                item.toEntity(namaProduk)
            }
            transaksiDao.insertItems(itemEntities)

            // 3. Catat Kas Masuk
            val kasEntry = KasEntity(
                id = UUID.randomUUID().toString(),
                jenis = "MASUK",
                kategoriId = "PENJUALAN",
                jumlah = transaksi.totalBelanja,
                keterangan = "Penjualan POS No. ${transaksi.nomorTransaksi}",
                referensiId = transaksi.id,
                referensiTipe = "TRANSAKSI",
                userId = transaksi.userId,
                tanggal = transaksi.createdAt.take(10),
                createdAt = transaksi.createdAt,
                isSynced = isOnline
            )
            kasDao.upsert(kasEntry)

            // 4. Unggah ke Supabase
            if (isOnline) {
                try {
                    remoteSource.createTransaksi(
                        transaksi.toDto(),
                        transaksi.items.map { it.toDto() }
                    )
                } catch (e: Exception) {
                    transaksiDao.upsert(transaksi.toEntity(isSynced = false))
                }
            }

            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Gagal memproses checkout belanja."))
        }
    }.flowOn(Dispatchers.IO)
}
