package com.koperasiku.app.presentation.pinjaman

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.koperasiku.app.core.extensions.toRupiah
import com.koperasiku.app.domain.model.Anggota
import com.koperasiku.app.presentation.ui.components.*
import com.koperasiku.app.presentation.ui.theme.KoperasiGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinjamanFormScreen(
    navController: NavController,
    viewModel: PinjamanViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var expandedAnggota by remember { mutableStateOf(false) }
    var expandedTenor by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.clearForm()
        viewModel.events.collect { event ->
            when (event) {
                is PinjamanEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                is PinjamanEvent.NavigateBack -> navController.popBackStack()
            }
        }
    }

    val tenorOptions = listOf(3, 6, 12, 24)

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Ajukan Pinjaman Baru",
                onBackClick = { navController.popBackStack() }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Anggota Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedAnggota,
                    onExpandedChange = { expandedAnggota = !expandedAnggota }
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = uiState.formAnggotaNama.ifEmpty { "Pilih Anggota..." },
                        onValueChange = {},
                        label = { Text("Anggota Pemohon") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedAnggota) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedAnggota,
                        onDismissRequest = { expandedAnggota = false }
                    ) {
                        uiState.anggotaList.forEach { anggota ->
                            DropdownMenuItem(
                                text = { Text("${anggota.nama} (${anggota.nomorAnggota})") },
                                onClick = {
                                    viewModel.onFormAnggotaSelected(anggota)
                                    expandedAnggota = false
                                }
                            )
                        }
                    }
                }

                // Loan amount
                CurrencyTextField(
                    value = uiState.formJumlah,
                    onValueChange = viewModel::onFormJumlahChanged,
                    label = "Jumlah Pinjaman (Rupiah)",
                    error = uiState.formJumlahError
                )

                // Tenor Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedTenor,
                    onExpandedChange = { expandedTenor = !expandedTenor }
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = "${uiState.formTenor} Bulan",
                        onValueChange = {},
                        label = { Text("Tenor Angsuran") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTenor) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedTenor,
                        onDismissRequest = { expandedTenor = false }
                    ) {
                        tenorOptions.forEach { tenor ->
                            DropdownMenuItem(
                                text = { Text("$tenor Bulan") },
                                onClick = {
                                    viewModel.onFormTenorChanged(tenor)
                                    expandedTenor = false
                                }
                            )
                        }
                    }
                }

                AppTextField(
                    value = uiState.formKeperluan,
                    onValueChange = viewModel::onFormKeperluanChanged,
                    label = "Keperluan Pinjaman",
                    modifier = Modifier.fillMaxWidth()
                )

                // Simulation Panel
                if (uiState.formJumlah > 0L) {
                    AppCard(backgroundColor = KoperasiGreen.copy(alpha = 0.06f)) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                "Simulasi Cicilan (Flat 1%/bln)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = KoperasiGreen
                            )
                            Spacer(Modifier.height(8.dp))
                            SimRow("Angsuran Pokok/bln", uiState.simulasiPokok.toRupiah())
                            SimRow("Bunga/bln (1%)", uiState.simulasiBunga.toRupiah())
                            Divider(modifier = Modifier.padding(vertical = 6.dp))
                            SimRow("Total Bayar/bln", uiState.simulasiPerBulan.toRupiah(), bold = true)
                            SimRow("Total Bunga ${uiState.formTenor}bln", uiState.simulasiTotalBunga.toRupiah())
                            SimRow("TOTAL KESELURUHAN", uiState.simulasiTotalBayar.toRupiah(), bold = true)
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                AppButton(
                    text = "Simpan Pengajuan",
                    onClick = viewModel::submitPengajuan,
                    isLoading = uiState.isLoading,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            LoadingOverlay(isVisible = uiState.isLoading)
        }
    }
}

@Composable
private fun SimRow(label: String, value: String, bold: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal
        )
    }
}
