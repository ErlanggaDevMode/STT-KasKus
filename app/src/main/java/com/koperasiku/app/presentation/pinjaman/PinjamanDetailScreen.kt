package com.koperasiku.app.presentation.pinjaman

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.koperasiku.app.core.extensions.toIndonesian
import com.koperasiku.app.core.extensions.toIndonesianDateTime
import com.koperasiku.app.core.extensions.toRupiah
import com.koperasiku.app.domain.model.Angsuran
import com.koperasiku.app.presentation.navigation.Screen
import com.koperasiku.app.presentation.ui.components.*
import com.koperasiku.app.presentation.ui.theme.KoperasiGreen
import com.koperasiku.app.presentation.ui.theme.KoperasiRed

@Composable
fun PinjamanDetailScreen(
    pinjamanId: String,
    navController: NavController,
    viewModel: PinjamanViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showApproveDialog by remember { mutableStateOf(false) }
    var showRejectDialog by remember { mutableStateOf(false) }

    LaunchedEffect(pinjamanId) {
        viewModel.loadPinjamanDetail(pinjamanId)
        viewModel.events.collect { event ->
            when (event) {
                is PinjamanEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                is PinjamanEvent.NavigateBack -> navController.popBackStack()
            }
        }
    }

    // Approve Confirmation Dialog
    if (showApproveDialog) {
        AppDialog(
            title = "Setujui Pinjaman",
            message = "Apakah Anda yakin ingin menyetujui dan mencairkan pinjaman ini? Dana akan otomatis tercatat sebagai kas keluar.",
            confirmText = "Setujui",
            onConfirm = {
                showApproveDialog = false
                viewModel.approvePinjaman(pinjamanId)
            },
            onDismiss = { showApproveDialog = false }
        )
    }

    // Reject Confirmation Dialog
    if (showRejectDialog) {
        AppDialog(
            title = "Tolak Pinjaman",
            message = "Apakah Anda yakin ingin menolak pengajuan pinjaman ini?",
            confirmText = "Tolak",
            isDestructive = true,
            onConfirm = {
                showRejectDialog = false
                viewModel.rejectPinjaman(pinjamanId)
            },
            onDismiss = { showRejectDialog = false }
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Detail Pinjaman",
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
            uiState.selectedPinjaman?.let { pinjaman ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Header info
                    item {
                        AppCard {
                            Column(Modifier.padding(16.dp)) {
                                Text(
                                    text = pinjaman.anggotaNama ?: pinjaman.anggotaId,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text("No. ${pinjaman.nomorPinjaman}", style = MaterialTheme.typography.bodyMedium)
                                Spacer(Modifier.height(12.dp))
                                Divider()
                                Spacer(Modifier.height(12.dp))
                                InfoRow("Jumlah Pinjaman", pinjaman.jumlahPinjaman.toRupiah())
                                InfoRow("Tenor", "${pinjaman.tenorBulan} Bulan")
                                InfoRow("Bunga / Bulan", "${pinjaman.bungaPersenPerBulan}% (Flat)")
                                InfoRow("Angsuran / Bulan", pinjaman.angsuranPerBulan.toRupiah())
                                InfoRow("Total Bayar", pinjaman.totalBayar.toRupiah())
                                InfoRow("Total Bunga", pinjaman.totalBunga.toRupiah())
                                InfoRow("Keperluan", pinjaman.keperluan ?: "-")
                                InfoRow("Tanggal Pengajuan", pinjaman.tanggalPengajuan)
                                if (pinjaman.tanggalDisetujui != null) {
                                    InfoRow("Tanggal Disetujui", pinjaman.tanggalDisetujui)
                                }
                                val statusColor = when (pinjaman.status) {
                                    "AKTIF" -> KoperasiGreen
                                    "DIAJUKAN" -> MaterialTheme.colorScheme.tertiary
                                    "DITOLAK" -> KoperasiRed
                                    else -> MaterialTheme.colorScheme.outline
                                }
                                Spacer(Modifier.height(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = statusColor.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        "Status: ${pinjaman.status}",
                                        color = statusColor,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.labelMedium,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Approve / Reject actions (only for DIAJUKAN status)
                    if (pinjaman.status == "DIAJUKAN") {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                AppButton(
                                    text = "Setujui",
                                    onClick = { showApproveDialog = true },
                                    modifier = Modifier.weight(1f)
                                )
                                AppButton(
                                    text = "Tolak",
                                    onClick = { showRejectDialog = true },
                                    variant = ButtonVariant.Outlined,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    // Installment schedule
                    item {
                        Text(
                            "Jadwal Angsuran",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    if (uiState.angsuranList.isEmpty()) {
                        item {
                            Text(
                                "Jadwal cicilan belum tersedia.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        items(uiState.angsuranList, key = { it.id }) { angsuran ->
                            AngsuranRowCard(
                                angsuran = angsuran,
                                onBayar = {
                                    viewModel.setSelectedAngsuran(angsuran)
                                    navController.navigate(Screen.BayarAngsuran.createRoute(angsuran.id))
                                }
                            )
                        }
                    }
                }
            } ?: run {
                if (!uiState.isLoading) {
                    EmptyState(message = "Data pinjaman tidak ditemukan.")
                }
            }

            LoadingOverlay(isVisible = uiState.isLoading)
        }
    }
}

@Composable
fun AngsuranRowCard(angsuran: Angsuran, onBayar: () -> Unit) {
    val isLunas = angsuran.status == "LUNAS"
    val statusColor = when (angsuran.status) {
        "LUNAS" -> KoperasiGreen
        "TERLAMBAT" -> KoperasiRed
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    AppCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Cicilan Ke-${angsuran.ke}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Jatuh Tempo: ${angsuran.tanggalJatuhTempo}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    angsuran.jumlahTotal.toRupiah(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = statusColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        angsuran.status.replace("_", " "),
                        color = statusColor,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                if (!isLunas) {
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = onBayar,
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("Bayar", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}
