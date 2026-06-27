package com.koperasiku.app.domain.usecase.simpanan

import com.koperasiku.app.core.utils.Resource
import com.koperasiku.app.domain.repository.SimpananRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DepositSimpananUseCase @Inject constructor(
    private val repository: SimpananRepository
) {
    operator fun invoke(
        anggotaId: String,
        jenisSimpanan: String,
        jumlah: Long,
        keterangan: String?
    ): Flow<Resource<Unit>> {
        return repository.executeSimpananTransaction(
            anggotaId = anggotaId,
            jenisTransaction = "SETORAN",
            jenisSimpanan = jenisSimpanan,
            jumlah = jumlah,
            keterangan = keterangan
        )
    }
}
