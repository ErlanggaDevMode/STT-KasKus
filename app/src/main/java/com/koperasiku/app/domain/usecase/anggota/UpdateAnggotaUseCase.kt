package com.koperasiku.app.domain.usecase.anggota

import com.koperasiku.app.core.utils.Resource
import com.koperasiku.app.domain.model.Anggota
import com.koperasiku.app.domain.repository.AnggotaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateAnggotaUseCase @Inject constructor(
    private val repository: AnggotaRepository
) {
    operator fun invoke(anggota: Anggota): Flow<Resource<Unit>> {
        return repository.updateAnggota(anggota)
    }
}
