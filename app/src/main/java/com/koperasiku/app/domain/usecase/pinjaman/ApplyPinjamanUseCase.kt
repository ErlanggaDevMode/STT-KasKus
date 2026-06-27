package com.koperasiku.app.domain.usecase.pinjaman

import com.koperasiku.app.core.utils.Resource
import com.koperasiku.app.domain.model.Pinjaman
import com.koperasiku.app.domain.repository.PinjamanRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ApplyPinjamanUseCase @Inject constructor(
    private val repository: PinjamanRepository
) {
    operator fun invoke(pinjaman: Pinjaman): Flow<Resource<Unit>> =
        repository.applyPinjaman(pinjaman)
}
