package com.koperasiku.app.domain.usecase.auth

import com.koperasiku.app.core.utils.Resource
import com.koperasiku.app.domain.model.User
import com.koperasiku.app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(email: String, password: String): Flow<Resource<User>> {
        return repository.login(email, password)
    }
}
