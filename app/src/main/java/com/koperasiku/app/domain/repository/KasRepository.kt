package com.koperasiku.app.domain.repository

import com.koperasiku.app.core.utils.Resource
import com.koperasiku.app.domain.model.Kas
import com.koperasiku.app.domain.model.KasSummary
import kotlinx.coroutines.flow.Flow

interface KasRepository {
    fun getKasList(): Flow<List<Kas>>
    fun getKasSummary(): Flow<KasSummary>
    fun createKasTransaction(kas: Kas): Flow<Resource<Unit>>
}
