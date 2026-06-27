package com.koperasiku.app.domain.usecase.kas

import com.koperasiku.app.core.utils.Resource
import com.koperasiku.app.domain.model.Kas
import com.koperasiku.app.domain.repository.KasRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateKasTransactionUseCase @Inject constructor(
    private val repository: KasRepository
) {
    operator fun invoke(kas: Kas): Flow<Resource<Unit>> = repository.createKasTransaction(kas)
}
