package com.koperasiku.app.presentation.anggota

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
fun AnggotaFormScreen(
    anggotaId: String?,
    navController: NavController,
    viewModel: AnggotaViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    LaunchedEffect(key1 = anggotaId) {
        if (anggotaId != null) {
            viewModel.loadAnggotaDetail(anggotaId)
        } else {
            viewModel.clearForm()
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.events.collect { event ->
            when (event) {
                is AnggotaEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is AnggotaEvent.NavigateBack -> {
                    navController.popBackStack()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = if (anggotaId == null) "Tambah Anggota" else "Edit Anggota",
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
                    .padding(24.dp)
                    .verticalScroll(scrollState)
            ) {
                AppTextField(
                    value = uiState.formNama,
                    onValueChange = viewModel::onFormNamaChanged,
                    label = "Nama Lengkap",
                    error = uiState.formNamaError,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                AppTextField(
                    value = uiState.formNik,
                    onValueChange = viewModel::onFormNikChanged,
                    label = "NIK (KTP)",
                    keyboardType = KeyboardType.Number,
                    error = uiState.formNikError,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                AppTextField(
                    value = uiState.formNoHp,
                    onValueChange = viewModel::onFormNoHpChanged,
                    label = "Nomor HP",
                    keyboardType = KeyboardType.Phone,
                    error = uiState.formNoHpError,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                AppTextField(
                    value = uiState.formAlamat,
                    onValueChange = viewModel::onFormAlamatChanged,
                    label = "Alamat Lengkap",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                AppButton(
                    text = "Simpan Data",
                    onClick = { viewModel.saveAnggota(anggotaId) },
                    isLoading = uiState.isLoading,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            LoadingOverlay(isVisible = uiState.isLoading)
        }
    }
}
