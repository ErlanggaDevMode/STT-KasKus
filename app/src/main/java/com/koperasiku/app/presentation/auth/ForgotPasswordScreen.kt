package com.koperasiku.app.presentation.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.koperasiku.app.presentation.ui.components.AppButton
import com.koperasiku.app.presentation.ui.components.AppTextField
import com.koperasiku.app.presentation.ui.components.AppTopBar
import com.koperasiku.app.presentation.ui.components.LoadingOverlay
import com.koperasiku.app.presentation.ui.theme.KopkustTheme

@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    viewModel: ForgotPasswordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.events.collect { event ->
            when (event) {
                is ForgotPasswordEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is ForgotPasswordEvent.NavigateBack -> {
                    navController.popBackStack()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Lupa Password",
                onBackClick = { navController.popBackStack() }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Masukkan alamat email Anda untuk menerima tautan pemulihan kata sandi.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                AppTextField(
                    value = uiState.email,
                    onValueChange = viewModel::onEmailChanged,
                    label = "Email",
                    keyboardType = KeyboardType.Email,
                    error = uiState.emailError,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                AppButton(
                    text = "Kirim Instruksi",
                    onClick = viewModel::resetPassword,
                    isLoading = uiState.isLoading,
                    modifier = Modifier.fillMaxWidth()
                )

                if (uiState.isSuccess) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Sukses! Cek inbox email Anda.",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            LoadingOverlay(isVisible = uiState.isLoading)
        }
    }
}
