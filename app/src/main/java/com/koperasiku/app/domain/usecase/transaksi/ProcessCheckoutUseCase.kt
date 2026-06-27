package com.koperasiku.app.domain.usecase.transaksi

import com.koperasiku.app.core.utils.Resource
import com.koperasiku.app.domain.model.Transaksi
import com.koperasiku.app.domain.repository.TransaksiRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProcessCheckoutUseCase @Inject constructor(
    private val repository: TransaksiRepository
) {
    operator fun invoke(transaksi: Transaksi): Flow<Resource<Unit>> {
        return repository.processCheckout(transaksi)
    }
}
