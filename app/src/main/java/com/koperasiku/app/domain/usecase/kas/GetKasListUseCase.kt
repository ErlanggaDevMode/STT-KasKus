package com.koperasiku.app.domain.usecase.kas

import com.koperasiku.app.domain.model.Kas
import com.koperasiku.app.domain.repository.KasRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetKasListUseCase @Inject constructor(
    private val repository: KasRepository
) {
    operator fun invoke(): Flow<List<Kas>> = repository.getKasList()
}
