package com.koperasiku.app.domain.usecase.produk

import com.koperasiku.app.core.utils.Resource
import com.koperasiku.app.domain.repository.ProdukRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateStokUseCase @Inject constructor(
    private val repository: ProdukRepository
) {
    operator fun invoke(
        produkId: String,
        jumlah: Int,
        jenis: String,
        keterangan: String?
    ): Flow<Resource<Unit>> {
        return repository.updateStok(produkId, jumlah, jenis, keterangan)
    }
}
