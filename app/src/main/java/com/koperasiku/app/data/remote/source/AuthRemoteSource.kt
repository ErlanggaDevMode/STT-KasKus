package com.koperasiku.app.data.remote.source

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import com.koperasiku.app.data.remote.dto.ProfileDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRemoteSource @Inject constructor(
    private val supabase: SupabaseClient
) {
    suspend fun signIn(emailInput: String, passwordInput: String): ProfileDto {
        // Sign in to Supabase
        supabase.auth.signInWith(Email) {
            email = emailInput
            password = passwordInput
        }

        val userId = supabase.auth.currentUserOrNull()?.id 
            ?: throw Exception("Sesi masuk gagal: User ID tidak ditemukan")

        return getProfile(userId)
    }

    suspend fun getProfile(userId: String): ProfileDto {
        return supabase.postgrest.from("profiles")
            .select {
                filter {
                    eq("id", userId)
                }
            }
            .decodeSingle<ProfileDto>()
    }

    suspend fun signOut() {
        supabase.auth.signOut()
    }

    suspend fun resetPassword(emailInput: String) {
        supabase.auth.resetPasswordForEmail(email = emailInput)
    }
}
