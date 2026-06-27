package com.koperasiku.app.presentation.simpanan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.koperasiku.app.core.extensions.toIndonesianDateTime
import com.koperasiku.app.core.extensions.toRupiah
import com.koperasiku.app.domain.model.MutasiSimpanan
import com.koperasiku.app.presentation.navigation.Screen
import com.koperasiku.app.presentation.ui.components.AppButton
import com.koperasiku.app.presentation.ui.components.AppCard
import com.koperasiku.app.presentation.ui.components.AppTopBar
import com.koperasiku.app.presentation.ui.components.ButtonVariant
import com.koperasiku.app.presentation.ui.components.LoadingOverlay
import com.koperasiku.app.presentation.ui.theme.KoperasiGreen
import com.koperasiku.app.presentation.ui.theme.KoperasiRed

@Composable
fun SimpananDetailScreen(
    anggotaId: String,
    navController: NavController,
    viewModel: SimpananViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = anggotaId) {
        viewModel.loadSimpananDetail(anggotaId)
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Detail Tabungan",
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
                    .padding(16.dp)
            ) {
                uiState.selectedAnggota?.let { anggota ->
                    Text(
                        text = anggota.nama,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Nomor Anggota: ${anggota.nomorAnggota}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Three types of savings cards
                val pokok = uiState.selectedSimpananDetails.firstOrNull { it.jenis == "POKOK" }?.saldo ?: 0L
                val wajib = uiState.selectedSimpananDetails.firstOrNull { it.jenis == "WAJIB" }?.saldo ?: 0L
                val sukarela = uiState.selectedSimpananDetails.firstOrNull { it.jenis == "SUKARELA" }?.saldo ?: 0L

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SavingsTypeCard(label = "Pokok", value = pokok, modifier = Modifier.weight(1f))
                    SavingsTypeCard(label = "Wajib", value = wajib, modifier = Modifier.weight(1f))
                    SavingsTypeCard(label = "Sukarela", value = sukarela, modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Big Total
                AppCard {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TOTAL SALDO TABUNGAN:",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = uiState.totalSimpanan.toRupiah(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AppButton(
                        text = "Setor Uang",
                        onClick = {
                            viewModel.clearForm()
                            viewModel.onFormJenisTrxChanged("SETORAN")
                            navController.navigate(Screen.SimpananForm.route.replace("{anggotaId}", anggotaId))
                        },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    AppButton(
                        text = "Tarik Uang",
                        onClick = {
                            viewModel.clearForm()
                            viewModel.onFormJenisTrxChanged("PENARIKAN")
                            navController.navigate(Screen.SimpananForm.route.replace("{anggotaId}", anggotaId))
                        },
                        variant = ButtonVariant.Outlined,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Riwayat Mutasi Tabungan",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (uiState.mutasiList.isEmpty()) {
                    Text(
                        text = "Belum ada transaksi setor/tarik pada rekening ini.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.mutasiList, key = { it.id }) { mutasi ->
                            MutasiSavingsRow(mutasi = mutasi)
                        }
                    }
                }
            }

            LoadingOverlay(isVisible = uiState.isLoading)
        }
    }
}

@Composable
fun SavingsTypeCard(
    label: String,
    value: Long,
    modifier: Modifier = Modifier
) {
    AppCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value.toRupiah(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun MutasiSavingsRow(
    mutasi: MutasiSimpanan
) {
    val isSetoran = mutasi.jenis == "SETORAN"
    val tintColor = if (isSetoran) KoperasiGreen else KoperasiRed
    val sign = if (isSetoran) "+" else "-"

    AppCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(36.dp)
                    .background(tintColor, shape = RoundedCornerShape(2.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${mutasi.jenis} Simpanan ${mutasi.jenisSimpanan}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = mutasi.createdAt.toIndonesianDateTime(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "$sign${mutasi.jumlah.toRupiah()}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = tintColor
                )
                Text(
                    text = "Saldo: ${mutasi.saldoSesudah.toRupiah()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
