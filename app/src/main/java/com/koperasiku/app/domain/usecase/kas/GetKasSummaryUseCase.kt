package com.koperasiku.app.domain.usecase.kas

import com.koperasiku.app.domain.model.KasSummary
import com.koperasiku.app.domain.repository.KasRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetKasSummaryUseCase @Inject constructor(
    private val repository: KasRepository
) {
    operator fun invoke(): Flow<KasSummary> = repository.getKasSummary()
}
