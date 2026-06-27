package com.koperasiku.app.presentation.pinjaman

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.koperasiku.app.core.extensions.toRupiah
import com.koperasiku.app.presentation.ui.components.*

@Composable
fun BayarAngsuranScreen(
    angsuranId: String,
    navController: NavController,
    viewModel: PinjamanViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val angsuran = uiState.selectedAngsuran

    LaunchedEffect(angsuranId) {
        viewModel.events.collect { event ->
            when (event) {
                is PinjamanEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                is PinjamanEvent.NavigateBack -> navController.popBackStack()
            }
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Bayar Angsuran",
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
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                angsuran?.let { a ->
                    AppCard {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                "Cicilan Ke-${a.ke}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(12.dp))
                            Divider()
                            Spacer(Modifier.height(12.dp))

                            PayRow("Angsuran Pokok", a.jumlahPokok.toRupiah())
                            PayRow("Bunga", a.jumlahBunga.toRupiah())
                            Divider(modifier = Modifier.padding(vertical = 6.dp))
                            PayRow("Total Angsuran", a.jumlahTotal.toRupiah(), bold = true)
                            PayRow("Jatuh Tempo", a.tanggalJatuhTempo)
                        }
                    }
                }

                CurrencyTextField(
                    value = uiState.formDenda,
                    onValueChange = viewModel::onFormDendaChanged,
                    label = "Denda Keterlambatan (Opsional)",
                    error = null
                )

                angsuran?.let { a ->
                    val totalBayar = a.jumlahTotal + uiState.formDenda
                    AppCard(backgroundColor = MaterialTheme.colorScheme.primaryContainer) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "TOTAL BAYAR:",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                totalBayar.toRupiah(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(Modifier.weight(1f))

                AppButton(
                    text = "Proses Pembayaran",
                    onClick = { viewModel.bayarAngsuran(angsuranId) },
                    isLoading = uiState.isLoading,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            LoadingOverlay(isVisible = uiState.isLoading)
        }
    }
}

@Composable
private fun PayRow(label: String, value: String, bold: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal
        )
    }
}
