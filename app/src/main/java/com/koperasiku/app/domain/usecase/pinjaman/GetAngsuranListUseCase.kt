package com.koperasiku.app.domain.usecase.pinjaman

import com.koperasiku.app.domain.model.Angsuran
import com.koperasiku.app.domain.repository.PinjamanRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAngsuranListUseCase @Inject constructor(
    private val repository: PinjamanRepository
) {
    operator fun invoke(pinjamanId: String): Flow<List<Angsuran>> =
        repository.getAngsuranList(pinjamanId)
}
