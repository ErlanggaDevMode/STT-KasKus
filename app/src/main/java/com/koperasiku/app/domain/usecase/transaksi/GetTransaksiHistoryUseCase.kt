package com.koperasiku.app.domain.usecase.transaksi

import com.koperasiku.app.domain.model.Transaksi
import com.koperasiku.app.domain.repository.TransaksiRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTransaksiHistoryUseCase @Inject constructor(
    private val repository: TransaksiRepository
) {
    operator fun invoke(): Flow<List<Transaksi>> {
        return repository.getTransaksiHistory()
    }
}
