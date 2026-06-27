package com.koperasiku.app.core.session

import android.content.Context
import android.content.SharedPreferences
import com.koperasiku.app.domain.model.User
import com.koperasiku.app.domain.model.enums.UserRole
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("koperasiku_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_NAMA = "nama"
        private const val KEY_NO_HP = "no_hp"
        private const val KEY_FOTO_URL = "foto_url"
        private const val KEY_ROLE = "role"
    }

    fun saveSession(user: User) {
        prefs.edit().apply {
            putString(KEY_USER_ID, user.id)
            putString(KEY_NAMA, user.nama)
            putString(KEY_NO_HP, user.noHp)
            putString(KEY_FOTO_URL, user.fotoUrl)
            putString(KEY_ROLE, user.role.name)
            apply()
        }
    }

    fun getSession(): User? {
        val id = prefs.getString(KEY_USER_ID, null) ?: return null
        val nama = prefs.getString(KEY_NAMA, "") ?: ""
        val noHp = prefs.getString(KEY_NO_HP, null)
        val fotoUrl = prefs.getString(KEY_FOTO_URL, null)
        val roleStr = prefs.getString(KEY_ROLE, null) ?: return null
        val role = try {
            UserRole.valueOf(roleStr)
        } catch (e: Exception) {
            UserRole.ANGGOTA
        }

        return User(id, nama, noHp, fotoUrl, role)
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    val currentUserId: String? get() = prefs.getString(KEY_USER_ID, null)
    val currentUserRole: UserRole? get() {
        val roleStr = prefs.getString(KEY_ROLE, null) ?: return null
        return try {
            UserRole.valueOf(roleStr)
        } catch (e: Exception) {
            null
        }
    }
}
