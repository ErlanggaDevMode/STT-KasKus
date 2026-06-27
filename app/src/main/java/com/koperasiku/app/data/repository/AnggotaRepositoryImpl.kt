package com.koperasiku.app.data.repository

import com.koperasiku.app.core.network.NetworkMonitor
import com.koperasiku.app.core.utils.Resource
import com.koperasiku.app.data.local.dao.AnggotaDao
import com.koperasiku.app.data.mapper.toDomain
import com.koperasiku.app.data.mapper.toEntity
import com.koperasiku.app.data.mapper.toDto
import com.koperasiku.app.data.remote.source.AnggotaRemoteSource
import com.koperasiku.app.domain.model.Anggota
import com.koperasiku.app.domain.repository.AnggotaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnggotaRepositoryImpl @Inject constructor(
    private val localDao: AnggotaDao,
    private val remoteSource: AnggotaRemoteSource,
    private val networkMonitor: NetworkMonitor
) : AnggotaRepository {

    override fun getAnggotaList(): Flow<List<Anggota>> {
        return localDao.getAll()
            .map { entities -> entities.map { it.toDomain() } }
            .onStart {
                if (networkMonitor.isOnline()) {
                    try {
                        val remoteList = remoteSource.getAnggotaList()
                        localDao.upsertAll(remoteList.map { it.toDomain().toEntity(isSynced = true) })
                    } catch (e: Exception) {
                        // Gagal sync tidak crash app — data lokal tetap ditampilkan
                    }
                }
            }
            .flowOn(Dispatchers.IO)
    }

    override fun searchAnggota(query: String): Flow<List<Anggota>> {
        return localDao.search(query)
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(Dispatchers.IO)
    }

    override fun getAnggotaDetail(id: String): Flow<Resource<Anggota>> = flow {
        emit(Resource.Loading)
        val cached = localDao.getById(id)
        if (cached != null) {
            emit(Resource.Success(cached.toDomain()))
        }

        if (networkMonitor.isOnline()) {
            try {
                val remoteDto = remoteSource.getAnggotaDetail(id)
                val domain = remoteDto.toDomain()
                localDao.upsert(domain.toEntity(isSynced = true))
                emit(Resource.Success(domain))
            } catch (e: Exception) {
                if (cached == null) {
                    emit(Resource.Error(e.message ?: "Gagal memuat detail anggota."))
                }
            }
        } else if (cached == null) {
            emit(Resource.Error("Koneksi internet tidak tersedia untuk mengambil data."))
        }
    }.flowOn(Dispatchers.IO)

    override fun createAnggota(anggota: Anggota): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        val hasInternet = networkMonitor.isOnline()
        val localEntity = anggota.toEntity(isSynced = hasInternet)
        localDao.upsert(localEntity)

        if (hasInternet) {
            try {
                remoteSource.createAnggota(anggota.toDto())
                emit(Resource.Success(Unit))
            } catch (e: Exception) {
                localDao.upsert(anggota.toEntity(isSynced = false))
                emit(Resource.Error(e.message ?: "Pendaftaran tersimpan lokal. Gagal mengunggah ke server."))
            }
        } else {
            emit(Resource.Success(Unit))
        }
    }.flowOn(Dispatchers.IO)

    override fun updateAnggota(anggota: Anggota): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        val hasInternet = networkMonitor.isOnline()
        val localEntity = anggota.toEntity(isSynced = hasInternet)
        localDao.upsert(localEntity)

        if (hasInternet) {
            try {
                remoteSource.updateAnggota(anggota.toDto())
                emit(Resource.Success(Unit))
            } catch (e: Exception) {
                localDao.upsert(anggota.toEntity(isSynced = false))
                emit(Resource.Error(e.message ?: "Perubahan tersimpan lokal. Gagal mengunggah ke server."))
            }
        } else {
            emit(Resource.Success(Unit))
        }
    }.flowOn(Dispatchers.IO)

    override fun deactivateAnggota(id: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        val hasInternet = networkMonitor.isOnline()
        localDao.deactivate(id)

        if (hasInternet) {
            try {
                remoteSource.deactivateAnggota(id)
                emit(Resource.Success(Unit))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Penonaktifan tersimpan lokal. Gagal memperbarui server."))
            }
        } else {
            emit(Resource.Success(Unit))
        }
    }.flowOn(Dispatchers.IO)
}
