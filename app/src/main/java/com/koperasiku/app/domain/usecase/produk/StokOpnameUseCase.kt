package com.koperasiku.app.domain.usecase.produk

import com.koperasiku.app.core.utils.Resource
import com.koperasiku.app.domain.repository.ProdukRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StokOpnameUseCase @Inject constructor(
    private val repository: ProdukRepository
) {
    operator fun invoke(
        opnameItems: Map<String, Int>,
        catatan: String?
    ): Flow<Resource<Unit>> {
        return repository.executeStokOpname(opnameItems, catatan)
    }
}
