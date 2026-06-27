package com.koperasiku.app.domain.usecase.auth

import com.koperasiku.app.core.utils.Resource
import com.koperasiku.app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(): Flow<Resource<Unit>> {
        return repository.logout()
    }
}
