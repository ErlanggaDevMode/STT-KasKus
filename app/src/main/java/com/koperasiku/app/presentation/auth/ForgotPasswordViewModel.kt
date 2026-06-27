package com.koperasiku.app.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koperasiku.app.core.utils.Resource
import com.koperasiku.app.domain.usecase.auth.ResetPasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ForgotPasswordUiState(
    val email: String = "",
    val emailError: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

sealed class ForgotPasswordEvent {
    data class ShowSnackbar(val message: String) : ForgotPasswordEvent()
    object NavigateBack : ForgotPasswordEvent()
}

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val resetPasswordUseCase: ResetPasswordUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    private val _events = Channel<ForgotPasswordEvent>(Channel.BUFFERED)
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

    fun resetPassword() {
        val email = _uiState.value.email.trim()

        if (email.isEmpty()) {
            _uiState.update { it.copy(emailError = "Email tidak boleh kosong") }
            return
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.update { it.copy(emailError = "Format email tidak valid") }
            return
        }

        viewModelScope.launch {
            resetPasswordUseCase(email).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                    }
                    is Resource.Success -> {
                        _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                        _events.send(ForgotPasswordEvent.ShowSnackbar("Tautan reset password telah dikirim ke email Anda"))
                    }
                    is Resource.Error -> {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                errorMessage = resource.message
                            )
                        }
                        _events.send(ForgotPasswordEvent.ShowSnackbar(resource.message))
                    }
                }
            }
        }
    }
}
