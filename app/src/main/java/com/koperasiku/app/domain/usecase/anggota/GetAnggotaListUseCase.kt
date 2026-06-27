package com.koperasiku.app.domain.usecase.anggota

import com.koperasiku.app.domain.model.Anggota
import com.koperasiku.app.domain.repository.AnggotaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAnggotaListUseCase @Inject constructor(
    private val repository: AnggotaRepository
) {
    operator fun invoke(): Flow<List<Anggota>> {
        return repository.getAnggotaList()
    }
}
