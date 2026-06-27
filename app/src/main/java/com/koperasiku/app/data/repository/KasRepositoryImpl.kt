package com.koperasiku.app.data.repository

import com.koperasiku.app.core.network.NetworkMonitor
import com.koperasiku.app.core.utils.Resource
import com.koperasiku.app.data.local.dao.KasDao
import com.koperasiku.app.data.mapper.toDomain
import com.koperasiku.app.data.mapper.toEntity
import com.koperasiku.app.data.mapper.toDto
import com.koperasiku.app.data.remote.source.KasRemoteSource
import com.koperasiku.app.domain.model.Kas
import com.koperasiku.app.domain.model.KasSummary
import com.koperasiku.app.domain.repository.KasRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KasRepositoryImpl @Inject constructor(
    private val kasDao: KasDao,
    private val remoteSource: KasRemoteSource,
    private val networkMonitor: NetworkMonitor
) : KasRepository {

    override fun getKasList(): Flow<List<Kas>> {
        return kasDao.getAll()
            .map { list -> list.map { it.toDomain() } }
            .onStart {
                if (networkMonitor.isOnline()) {
                    try {
                        val remoteList = remoteSource.getKasList()
                        kasDao.upsertAll(remoteList.map { it.toDomain().toEntity(isSynced = true) })
                    } catch (e: Exception) {
                        // Suppress background sync error
                    }
                }
            }
            .flowOn(Dispatchers.IO)
    }

    override fun getKasSummary(): Flow<KasSummary> {
        return kasDao.getTotalMasuk().combine(kasDao.getTotalKeluar()) { masuk, keluar ->
            KasSummary(
                saldoTotal = masuk - keluar,
                totalMasuk = masuk,
                totalKeluar = keluar
            )
        }.flowOn(Dispatchers.IO)
    }

    override fun createKasTransaction(kas: Kas): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        val hasInternet = networkMonitor.isOnline()
        kasDao.upsert(kas.toEntity(isSynced = hasInternet))

        if (hasInternet) {
            try {
                remoteSource.createKasTransaction(kas.toDto())
                emit(Resource.Success(Unit))
            } catch (e: Exception) {
                kasDao.upsert(kas.toEntity(isSynced = false))
                emit(Resource.Error(e.message ?: "Transaksi tersimpan lokal. Gagal diunggah ke server."))
            }
        } else {
            emit(Resource.Success(Unit))
        }
    }.flowOn(Dispatchers.IO)
}
