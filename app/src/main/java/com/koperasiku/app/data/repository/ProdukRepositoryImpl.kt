package com.koperasiku.app.data.repository

import com.koperasiku.app.core.network.NetworkMonitor
import com.koperasiku.app.core.session.SessionManager
import com.koperasiku.app.core.utils.Resource
import com.koperasiku.app.data.local.dao.MutasiStokDao
import com.koperasiku.app.data.local.dao.ProdukDao
import com.koperasiku.app.data.mapper.toDomain
import com.koperasiku.app.data.mapper.toEntity
import com.koperasiku.app.data.mapper.toDto
import com.koperasiku.app.data.remote.source.ProdukRemoteSource
import com.koperasiku.app.domain.model.MutasiStok
import com.koperasiku.app.domain.model.Produk
import com.koperasiku.app.domain.repository.ProdukRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.datetime.Clock
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProdukRepositoryImpl @Inject constructor(
    private val produkDao: ProdukDao,
    private val mutasiDao: MutasiStokDao,
    private val remoteSource: ProdukRemoteSource,
    private val networkMonitor: NetworkMonitor,
    private val sessionManager: SessionManager
) : ProdukRepository {

    override fun getProdukList(): Flow<List<Produk>> {
        return produkDao.getAll()
            .map { entities -> entities.map { it.toDomain() } }
            .onStart {
                if (networkMonitor.isOnline()) {
                    try {
                        val remoteList = remoteSource.getProdukList()
                        produkDao.upsertAll(remoteList.map { it.toDomain().toEntity(isSynced = true) })
                    } catch (e: Exception) {
                        // Suppress background sync error
                    }
                }
            }
            .flowOn(Dispatchers.IO)
    }

    override fun getStokMenipis(): Flow<List<Produk>> {
        return produkDao.getStokMenipis()
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(Dispatchers.IO)
    }

    override fun searchProduk(query: String): Flow<List<Produk>> {
        return produkDao.search(query)
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(Dispatchers.IO)
    }

    override fun getProdukDetail(id: String): Flow<Resource<Produk>> = flow {
        emit(Resource.Loading)
        val cached = produkDao.getById(id)
        if (cached != null) {
            emit(Resource.Success(cached.toDomain()))
        }

        if (networkMonitor.isOnline()) {
            try {
                val remoteDto = remoteSource.getProdukDetail(id)
                val domain = remoteDto.toDomain()
                produkDao.upsert(domain.toEntity(isSynced = true))
                emit(Resource.Success(domain))
            } catch (e: Exception) {
                if (cached == null) {
                    emit(Resource.Error(e.message ?: "Gagal memuat detail produk."))
                }
            }
        } else if (cached == null) {
            emit(Resource.Error("Koneksi internet tidak tersedia untuk mengambil data."))
        }
    }.flowOn(Dispatchers.IO)

    override fun createProduk(produk: Produk): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        val hasInternet = networkMonitor.isOnline()
        produkDao.upsert(produk.toEntity(isSynced = hasInternet))

        if (hasInternet) {
            try {
                remoteSource.createProduk(produk.toDto())
                emit(Resource.Success(Unit))
            } catch (e: Exception) {
                produkDao.upsert(produk.toEntity(isSynced = false))
                emit(Resource.Error(e.message ?: "Produk disimpan lokal. Gagal diunggah ke server."))
            }
        } else {
            emit(Resource.Success(Unit))
        }
    }.flowOn(Dispatchers.IO)

    override fun updateProduk(produk: Produk): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        val hasInternet = networkMonitor.isOnline()
        produkDao.upsert(produk.toEntity(isSynced = hasInternet))

        if (hasInternet) {
            try {
                remoteSource.updateProduk(produk.toDto())
                emit(Resource.Success(Unit))
            } catch (e: Exception) {
                produkDao.upsert(produk.toEntity(isSynced = false))
                emit(Resource.Error(e.message ?: "Perubahan disimpan lokal. Gagal memperbarui server."))
            }
        } else {
            emit(Resource.Success(Unit))
        }
    }.flowOn(Dispatchers.IO)

    override fun updateStok(
        produkId: String,
        jumlah: Int,
        jenis: String,
        keterangan: String?
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            val productEntity = produkDao.getById(produkId)
                ?: throw Exception("Produk tidak ditemukan")
            val currentStok = productEntity.stokSaatIni
            val newStok = currentStok + jumlah

            // 1. Update Produk Local
            produkDao.updateStok(produkId, newStok)

            // 2. Create Mutasi Stok Local
            val mutasi = MutasiStok(
                id = UUID.randomUUID().toString(),
                produkId = produkId,
                jenis = jenis,
                jumlah = jumlah,
                stokSebelum = currentStok,
                stokSesudah = newStok,
                referensiId = null,
                referensiTipe = "MANUAL",
                keterangan = keterangan,
                userId = sessionManager.currentUserId,
                createdAt = Clock.System.now().toString()
            )
            mutasiDao.upsert(mutasi.toEntity())

            // 3. Sync Remote
            if (networkMonitor.isOnline()) {
                remoteSource.updateStok(produkId, newStok)
                remoteSource.createMutasiStok(mutasi.toDto())
            }
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Gagal memperbarui stok barang."))
        }
    }.flowOn(Dispatchers.IO)

    override fun executeStokOpname(
        opnameItems: Map<String, Int>,
        catatan: String?
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            val isOnline = networkMonitor.isOnline()
            opnameItems.forEach { (produkId, stokFisik) ->
                val cachedProduct = produkDao.getById(produkId) ?: return@forEach
                val stokSistem = cachedProduct.stokSaatIni
                val selisih = stokFisik - stokSistem

                if (selisih != 0) {
                    // Update Local Stock
                    produkDao.updateStok(produkId, stokFisik)

                    // Write Local Mutasi
                    val mutasi = MutasiStok(
                        id = UUID.randomUUID().toString(),
                        produkId = produkId,
                        jenis = "OPNAME",
                        jumlah = selisih,
                        stokSebelum = stokSistem,
                        stokSesudah = stokFisik,
                        referensiId = null,
                        referensiTipe = "OPNAME",
                        keterangan = catatan ?: "Stok Opname Penyesuaian",
                        userId = sessionManager.currentUserId,
                        createdAt = Clock.System.now().toString()
                    )
                    mutasiDao.upsert(mutasi.toEntity())

                    // Write Remote if Online
                    if (isOnline) {
                        remoteSource.updateStok(produkId, stokFisik)
                        remoteSource.createMutasiStok(mutasi.toDto())
                    }
                }
            }
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Gagal menyelesaikan stok opname."))
        }
    }.flowOn(Dispatchers.IO)

    override fun getMutasiStok(produkId: String): Flow<List<MutasiStok>> {
        return mutasiDao.getByProdukId(produkId)
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(Dispatchers.IO)
    }
}
