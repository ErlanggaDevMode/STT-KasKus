package com.koperasiku.app.data.repository

import com.koperasiku.app.core.session.SessionManager
import com.koperasiku.app.core.utils.Resource
import com.koperasiku.app.data.remote.dto.ProfileDto
import com.koperasiku.app.data.remote.source.AuthRemoteSource
import com.koperasiku.app.domain.model.User
import com.koperasiku.app.domain.model.enums.UserRole
import com.koperasiku.app.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val remoteSource: AuthRemoteSource,
    private val sessionManager: SessionManager
) : AuthRepository {

    override fun login(email: String, password: String): Flow<Resource<User>> = flow {
        emit(Resource.Loading)
        try {
            val profileDto = remoteSource.signIn(email, password)
            val user = profileDto.toDomain()
            sessionManager.saveSession(user)
            emit(Resource.Success(user))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Gagal masuk. Periksa kembali email, kata sandi, atau koneksi Anda."))
        }
    }.flowOn(Dispatchers.IO)

    override fun logout(): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            remoteSource.signOut()
            sessionManager.clearSession()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            // Clear local cache even if remote request fails
            sessionManager.clearSession()
            emit(Resource.Success(Unit))
        }
    }.flowOn(Dispatchers.IO)

    override fun resetPassword(email: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            remoteSource.resetPassword(email)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Gagal mengirimkan email reset kata sandi."))
        }
    }.flowOn(Dispatchers.IO)

    override fun getCurrentUser(): Flow<Resource<User?>> = flow {
        emit(Resource.Loading)
        val cachedUser = sessionManager.getSession()
        if (cachedUser != null) {
            emit(Resource.Success(cachedUser))
            try {
                val profileDto = remoteSource.getProfile(cachedUser.id)
                val refreshedUser = profileDto.toDomain()
                sessionManager.saveSession(refreshedUser)
                emit(Resource.Success(refreshedUser))
            } catch (e: Exception) {
                // Keep cached user if refresh fails
            }
        } else {
            emit(Resource.Success(null))
        }
    }.flowOn(Dispatchers.IO)

    private fun ProfileDto.toDomain(): User {
        val userRole = try {
            UserRole.valueOf(this.role.uppercase())
        } catch (e: Exception) {
            UserRole.ANGGOTA
        }
        return User(
            id = this.id,
            nama = this.nama,
            noHp = this.noHp,
            fotoUrl = this.fotoUrl,
            role = userRole
        )
    }
}
