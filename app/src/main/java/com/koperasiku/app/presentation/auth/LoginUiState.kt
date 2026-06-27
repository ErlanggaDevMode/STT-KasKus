package com.koperasiku.app.presentation.auth

import com.koperasiku.app.domain.model.enums.UserRole

data class LoginUiState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed class LoginEvent {
    data class Success(val role: UserRole) : LoginEvent()
    data class ShowSnackbar(val message: String) : LoginEvent()
}
