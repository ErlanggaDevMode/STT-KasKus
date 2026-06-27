package com.koperasiku.app.presentation.stok

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import com.koperasiku.app.presentation.ui.components.CurrencyTextField
import com.koperasiku.app.presentation.ui.components.LoadingOverlay
import com.koperasiku.app.presentation.ui.theme.KopkustTheme

@Composable
fun ProdukFormScreen(
    produkId: String?,
    navController: NavController,
    viewModel: StokViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    LaunchedEffect(key1 = produkId) {
        if (produkId != null) {
            viewModel.loadProdukDetail(produkId)
        } else {
            viewModel.clearForm()
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.events.collect { event ->
            when (event) {
                is StokEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is StokEvent.NavigateBack -> {
                    navController.popBackStack()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = if (produkId == null) "Tambah Produk" else "Edit Produk",
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
                    value = uiState.formKode,
                    onValueChange = viewModel::onFormKodeChanged,
                    label = "Kode Produk",
                    error = uiState.formKodeError,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                AppTextField(
                    value = uiState.formNama,
                    onValueChange = viewModel::onFormNamaChanged,
                    label = "Nama Produk",
                    error = uiState.formNamaError,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                CurrencyTextField(
                    value = uiState.formHargaBeli,
                    onValueChange = viewModel::onFormHargaBeliChanged,
                    label = "Harga Beli"
                )

                Spacer(modifier = Modifier.height(16.dp))

                CurrencyTextField(
                    value = uiState.formHargaJual,
                    onValueChange = viewModel::onFormHargaJualChanged,
                    label = "Harga Jual"
                )

                Spacer(modifier = Modifier.height(16.dp))

                AppTextField(
                    value = uiState.formSatuan,
                    onValueChange = viewModel::onFormSatuanChanged,
                    label = "Satuan (misal: pcs, box, sachet)",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Minimum Stock input
                OutlinedTextField(
                    value = if (uiState.formMinimumStok == 0) "" else uiState.formMinimumStok.toString(),
                    onValueChange = {
                        val intValue = it.filter { char -> char.isDigit() }.toIntOrNull() ?: 0
                        viewModel.onFormMinimumStokChanged(intValue)
                    },
                    label = { Text("Batas Stok Minimum") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                )

                // Initial Stock input (only show when creating new product)
                if (produkId == null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = if (uiState.formStokAwal == 0) "" else uiState.formStokAwal.toString(),
                        onValueChange = {
                            val intValue = it.filter { char -> char.isDigit() }.toIntOrNull() ?: 0
                            viewModel.onFormStokAwalChanged(intValue)
                        },
                        label = { Text("Stok Awal") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                AppButton(
                    text = "Simpan Data",
                    onClick = { viewModel.saveProduk(produkId) },
                    isLoading = uiState.isLoading,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            LoadingOverlay(isVisible = uiState.isLoading)
        }
    }
}
