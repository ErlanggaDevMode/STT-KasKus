package com.koperasiku.app.presentation.pinjaman

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.koperasiku.app.core.extensions.toIndonesian
import com.koperasiku.app.core.extensions.toRupiah
import com.koperasiku.app.domain.model.Pinjaman
import com.koperasiku.app.presentation.navigation.Screen
import com.koperasiku.app.presentation.ui.components.*
import com.koperasiku.app.presentation.ui.theme.KoperasiGreen
import com.koperasiku.app.presentation.ui.theme.KoperasiRed

@Composable
fun PinjamanScreen(
    navController: NavController,
    viewModel: PinjamanViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.loadPinjamanList()
        viewModel.events.collect { event ->
            when (event) {
                is PinjamanEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = { AppTopBar(title = "Pinjaman Anggota") },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.clearForm()
                    navController.navigate(Screen.PinjamanForm.route)
                },
                containerColor = KoperasiGreen,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Ajukan Pinjaman")
            }
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
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(Modifier.height(12.dp))

                // Summary Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppCard(
                        modifier = Modifier.weight(1f),
                        backgroundColor = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Pinjaman Aktif", style = MaterialTheme.typography.bodySmall)
                            Text(
                                uiState.totalPinjamanAktif.toRupiah(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    AppCard(
                        modifier = Modifier.weight(1f),
                        backgroundColor = KoperasiRed.copy(alpha = 0.08f)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Cicilan/Bulan", style = MaterialTheme.typography.bodySmall)
                            Text(
                                uiState.totalTunggakan.toRupiah(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = KoperasiRed
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Filter tabs
                val filters = listOf("SEMUA", "DIAJUKAN", "AKTIF", "LUNAS", "DITOLAK")
                ScrollableTabRow(
                    selectedTabIndex = filters.indexOf(uiState.selectedFilter),
                    modifier = Modifier.fillMaxWidth(),
                    edgePadding = 0.dp
                ) {
                    filters.forEach { filter ->
                        Tab(
                            selected = uiState.selectedFilter == filter,
                            onClick = { viewModel.onFilterChanged(filter) },
                            text = {
                                Text(
                                    text = filter.replace("_", " "),
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                if (uiState.filteredList.isEmpty() && !uiState.isLoading) {
                    EmptyState(message = "Tidak ada data pinjaman ditemukan.")
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.filteredList, key = { it.id }) { pinjaman ->
                            PinjamanItemCard(
                                pinjaman = pinjaman,
                                onClick = { navController.navigate(Screen.PinjamanDetail.createRoute(pinjaman.id)) }
                            )
                        }
                    }
                }
            }
            LoadingOverlay(isVisible = uiState.isLoading)
        }
    }
}

@Composable
fun PinjamanItemCard(pinjaman: Pinjaman, onClick: () -> Unit) {
    val statusColor = when (pinjaman.status) {
        "AKTIF" -> KoperasiGreen
        "DIAJUKAN" -> MaterialTheme.colorScheme.tertiary
        "DITOLAK" -> KoperasiRed
        else -> MaterialTheme.colorScheme.outline
    }

    AppCard(onClick = onClick) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(48.dp)
                    .background(statusColor, shape = RoundedCornerShape(2.dp))
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pinjaman.anggotaNama ?: pinjaman.anggotaId,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "No. ${pinjaman.nomorPinjaman}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${pinjaman.tenorBulan} bulan • ${pinjaman.angsuranPerBulan.toRupiah()}/bln",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = pinjaman.jumlahPinjaman.toRupiah(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = statusColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = pinjaman.status,
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
