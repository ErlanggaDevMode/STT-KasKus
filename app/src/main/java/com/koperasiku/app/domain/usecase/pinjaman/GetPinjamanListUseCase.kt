package com.koperasiku.app.domain.usecase.pinjaman

import com.koperasiku.app.domain.model.Pinjaman
import com.koperasiku.app.domain.repository.PinjamanRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPinjamanListUseCase @Inject constructor(
    private val repository: PinjamanRepository
) {
    operator fun invoke(): Flow<List<Pinjaman>> = repository.getPinjamanList()
}
