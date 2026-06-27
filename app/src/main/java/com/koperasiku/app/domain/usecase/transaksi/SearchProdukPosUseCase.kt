package com.koperasiku.app.domain.usecase.transaksi

import com.koperasiku.app.domain.model.Produk
import com.koperasiku.app.domain.repository.ProdukRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchProdukPosUseCase @Inject constructor(
    private val produkRepository: ProdukRepository
) {
    operator fun invoke(query: String): Flow<List<Produk>> {
        return produkRepository.searchProduk(query)
    }
}
