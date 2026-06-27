package com.koperasiku.app.domain.usecase.produk

import com.koperasiku.app.domain.model.Produk
import com.koperasiku.app.domain.repository.ProdukRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProdukStokMenipisUseCase @Inject constructor(
    private val repository: ProdukRepository
) {
    operator fun invoke(): Flow<List<Produk>> = repository.getStokMenipis()
}
