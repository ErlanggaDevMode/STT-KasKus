package com.koperasiku.app.presentation.kas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.koperasiku.app.presentation.ui.components.AppButton
import com.koperasiku.app.presentation.ui.components.AppTextField
import com.koperasiku.app.presentation.ui.components.AppTopBar
import com.koperasiku.app.presentation.ui.components.CurrencyTextField
import com.koperasiku.app.presentation.ui.components.LoadingOverlay
import com.koperasiku.app.presentation.ui.theme.KopkustTheme
import com.koperasiku.app.presentation.ui.theme.KoperasiGreen
import com.koperasiku.app.presentation.ui.theme.KoperasiRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KasFormScreen(
    navController: NavController,
    viewModel: KasViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var expandedDropdown by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.clearForm()
        viewModel.events.collect { event ->
            when (event) {
                is KasEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is KasEvent.NavigateBack -> {
                    navController.popBackStack()
                }
            }
        }
    }

    val categories = if (uiState.formJenis == "MASUK") {
        listOf("PENDAPATAN", "PENYESUAIAN")
    } else {
        listOf("PENGELUARAN", "OPERASIONAL", "PENYESUAIAN")
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Tambah Kas Baru",
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
            ) {
                // Jenis Transaksi Toggle Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val activeBg = if (uiState.formJenis == "MASUK") KoperasiGreen else KoperasiRed
                    
                    Button(
                        onClick = { viewModel.onFormJenisChanged("MASUK") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (uiState.formJenis == "MASUK") activeBg else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (uiState.formJenis == "MASUK") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Uang Masuk")
                    }

                    Button(
                        onClick = { viewModel.onFormJenisChanged("KELUAR") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (uiState.formJenis == "KELUAR") activeBg else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (uiState.formJenis == "KELUAR") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Uang Keluar")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Kategori Dropdown List
                ExposedDropdownMenuBox(
                    expanded = expandedDropdown,
                    onExpandedChange = { expandedDropdown = !expandedDropdown }
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = uiState.formKategori,
                        onValueChange = {},
                        label = { Text("Kategori Transaksi") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedDropdown,
                        onDismissRequest = { expandedDropdown = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    viewModel.onFormKategoriChanged(category)
                                    expandedDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Currency Amount input
                CurrencyTextField(
                    value = uiState.formJumlah,
                    onValueChange = viewModel::onFormJumlahChanged,
                    label = "Nominal Jumlah (Rupiah)",
                    error = uiState.formJumlahError
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Keterangan input
                AppTextField(
                    value = uiState.formKeterangan,
                    onValueChange = viewModel::onFormKeteranganChanged,
                    label = "Keterangan Transaksi",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                AppButton(
                    text = "Simpan Transaksi",
                    onClick = viewModel::saveKasTransaction,
                    isLoading = uiState.isLoading,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            LoadingOverlay(isVisible = uiState.isLoading)
        }
    }
}
