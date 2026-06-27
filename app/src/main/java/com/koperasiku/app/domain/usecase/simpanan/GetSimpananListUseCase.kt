package com.koperasiku.app.domain.usecase.simpanan

import com.koperasiku.app.domain.model.Simpanan
import com.koperasiku.app.domain.repository.SimpananRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSimpananListUseCase @Inject constructor(
    private val repository: SimpananRepository
) {
    operator fun invoke(): Flow<List<Simpanan>> = repository.getSimpananList()
}
