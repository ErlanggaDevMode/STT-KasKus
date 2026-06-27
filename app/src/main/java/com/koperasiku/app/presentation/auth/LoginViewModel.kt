package com.koperasiku.app.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koperasiku.app.core.utils.Resource
import com.koperasiku.app.domain.usecase.auth.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _events = Channel<LoginEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onEmailChanged(email: String) {
        _uiState.update { 
            it.copy(
                email = email,
                emailError = null,
                errorMessage = null
            )
        }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { 
            it.copy(
                password = password,
                passwordError = null,
                errorMessage = null
            )
        }
    }

    fun login() {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password.trim()

        var hasError = false
        if (email.isEmpty()) {
            _uiState.update { it.copy(emailError = "Email tidak boleh kosong") }
            hasError = true
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.update { it.copy(emailError = "Format email tidak valid") }
            hasError = true
        }

        if (password.isEmpty()) {
            _uiState.update { it.copy(passwordError = "Kata sandi tidak boleh kosong") }
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            loginUseCase(email, password).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                    }
                    is Resource.Success -> {
                        _uiState.update { it.copy(isLoading = false) }
                        _events.send(LoginEvent.Success(resource.data.role))
                    }
                    is Resource.Error -> {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                errorMessage = resource.message
                            )
                        }
                        _events.send(LoginEvent.ShowSnackbar(resource.message))
                    }
                }
            }
        }
    }
}
