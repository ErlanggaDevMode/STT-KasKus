package com.koperasiku.app.domain.usecase.pinjaman

import com.koperasiku.app.core.utils.Resource
import com.koperasiku.app.domain.repository.PinjamanRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RejectPinjamanUseCase @Inject constructor(
    private val repository: PinjamanRepository
) {
    operator fun invoke(pinjamanId: String): Flow<Resource<Unit>> =
        repository.rejectPinjaman(pinjamanId)
}
