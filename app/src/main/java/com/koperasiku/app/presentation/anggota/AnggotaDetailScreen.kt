package com.koperasiku.app.presentation.anggota

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.koperasiku.app.core.extensions.toIndonesian
import com.koperasiku.app.presentation.navigation.Screen
import com.koperasiku.app.presentation.ui.components.AppButton
import com.koperasiku.app.presentation.ui.components.AppCard
import com.koperasiku.app.presentation.ui.components.AppTopBar
import com.koperasiku.app.presentation.ui.components.ButtonVariant
import com.koperasiku.app.presentation.ui.components.ConfirmDialog
import com.koperasiku.app.presentation.ui.components.LoadingOverlay

@Composable
fun AnggotaDetailScreen(
    anggotaId: String,
    navController: NavController,
    viewModel: AnggotaViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = anggotaId) {
        viewModel.loadAnggotaDetail(anggotaId)
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
                title = "Detail Anggota",
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
            uiState.selectedAnggota?.let { anggota ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    AppCard {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            DetailItem(label = "Nomor Anggota", value = anggota.nomorAnggota)
                            DetailItem(label = "Nama Lengkap", value = anggota.nama)
                            DetailItem(label = "NIK", value = anggota.nik)
                            DetailItem(label = "Nomor Handphone", value = anggota.noHp ?: "-")
                            DetailItem(label = "Alamat", value = anggota.alamat ?: "-")
                            DetailItem(label = "Tanggal Bergabung", value = anggota.tanggalGabung.toIndonesian())
                            DetailItem(label = "Status Keanggotaan", value = if (anggota.isAktif) "Aktif" else "Nonaktif")
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AppButton(
                            text = "Edit Profil",
                            onClick = {
                                navController.navigate(Screen.AnggotaForm.route.replace("{id}", anggota.id))
                            },
                            variant = ButtonVariant.Outlined,
                            modifier = Modifier.weight(1f)
                        )
                        if (anggota.isAktif) {
                            Spacer(modifier = Modifier.width(16.dp))
                            AppButton(
                                text = "Nonaktifkan",
                                onClick = { showDeleteConfirm = true },
                                variant = ButtonVariant.Danger,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            LoadingOverlay(isVisible = uiState.isLoading)

            if (showDeleteConfirm) {
                ConfirmDialog(
                    title = "Nonaktifkan Anggota",
                    message = "Apakah Anda yakin ingin menonaktifkan anggota ini? Data transaksi keuangan anggota akan tetap disimpan.",
                    onConfirm = {
                        showDeleteConfirm = false
                        viewModel.deactivateAnggota(anggotaId)
                    },
                    onDismiss = { showDeleteConfirm = false }
                )
            }
        }
    }
}

@Composable
fun DetailItem(
    label: String,
    value: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    }
}
