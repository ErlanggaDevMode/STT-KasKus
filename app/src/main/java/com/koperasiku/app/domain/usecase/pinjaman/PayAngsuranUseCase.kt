package com.koperasiku.app.domain.usecase.pinjaman

import com.koperasiku.app.core.utils.Resource
import com.koperasiku.app.domain.repository.PinjamanRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PayAngsuranUseCase @Inject constructor(
    private val repository: PinjamanRepository
) {
    operator fun invoke(angsuranId: String, jumlahDenda: Long = 0L): Flow<Resource<Unit>> =
        repository.payAngsuran(angsuranId, jumlahDenda)
}
