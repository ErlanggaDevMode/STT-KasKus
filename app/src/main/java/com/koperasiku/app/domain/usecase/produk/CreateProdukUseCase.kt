package com.koperasiku.app.domain.usecase.produk

import com.koperasiku.app.core.utils.Resource
import com.koperasiku.app.domain.model.Produk
import com.koperasiku.app.domain.repository.ProdukRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateProdukUseCase @Inject constructor(
    private val repository: ProdukRepository
) {
    operator fun invoke(produk: Produk): Flow<Resource<Unit>> = repository.createProduk(produk)
}
