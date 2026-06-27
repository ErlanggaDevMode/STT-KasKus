package com.koperasiku.app.domain.repository

import com.koperasiku.app.core.utils.Resource
import com.koperasiku.app.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun login(email: String, password: String): Flow<Resource<User>>
    fun logout(): Flow<Resource<Unit>>
    fun resetPassword(email: String): Flow<Resource<Unit>>
    fun getCurrentUser(): Flow<Resource<User?>>
}
