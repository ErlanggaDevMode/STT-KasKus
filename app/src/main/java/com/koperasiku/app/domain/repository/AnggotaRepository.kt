package com.koperasiku.app.domain.repository

import com.koperasiku.app.core.utils.Resource
import com.koperasiku.app.domain.model.Anggota
import kotlinx.coroutines.flow.Flow

interface AnggotaRepository {
    fun getAnggotaList(): Flow<List<Anggota>>
    fun searchAnggota(query: String): Flow<List<Anggota>>
    fun getAnggotaDetail(id: String): Flow<Resource<Anggota>>
    fun createAnggota(anggota: Anggota): Flow<Resource<Unit>>
    fun updateAnggota(anggota: Anggota): Flow<Resource<Unit>>
    fun deactivateAnggota(id: String): Flow<Resource<Unit>>
}
