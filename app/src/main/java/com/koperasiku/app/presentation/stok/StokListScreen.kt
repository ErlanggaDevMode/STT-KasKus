package com.koperasiku.app.presentation.stok

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.koperasiku.app.core.extensions.toRupiah
import com.koperasiku.app.domain.model.Produk
import com.koperasiku.app.presentation.navigation.Screen
import com.koperasiku.app.presentation.ui.components.AppCard
import com.koperasiku.app.presentation.ui.components.AppTopBar
import com.koperasiku.app.presentation.ui.components.EmptyState
import com.koperasiku.app.presentation.ui.components.LoadingOverlay
import com.koperasiku.app.presentation.ui.components.SearchBar
import com.koperasiku.app.presentation.ui.components.StokBadge
import com.koperasiku.app.presentation.ui.theme.KopkustTheme
import com.koperasiku.app.presentation.ui.theme.KoperasiGreen

@Composable
fun StokListScreen(
    navController: NavController,
    viewModel: StokViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedFilterIndex by remember { mutableStateOf(0) } // 0 = Semua, 1 = Menipis

    LaunchedEffect(key1 = true) {
        viewModel.loadProdukList()
        viewModel.events.collect { event ->
            when (event) {
                is StokEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Stok & Inventori",
                actions = {
                    IconButton(onClick = {
                        viewModel.initializeOpname(uiState.produkList)
                        navController.navigate(Screen.StokOpname.route)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Assignment,
                            contentDescription = "Stok Opname",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.clearForm()
                    navController.navigate(Screen.ProdukForm.route)
                },
                containerColor = KoperasiGreen,
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Produk")
            }
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
                    .padding(horizontal = 16.dp)
            ) {
                SearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = viewModel::onSearchQueryChanged,
                    placeholder = "Cari kode atau nama produk...",
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                // Tab Filter
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val activeBgColor = KoperasiGreen
                    val inactiveBgColor = MaterialTheme.colorScheme.surfaceVariant

                    Button(
                        onClick = {
                            selectedFilterIndex = 0
                            viewModel.loadProdukList()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedFilterIndex == 0) activeBgColor else inactiveBgColor,
                            contentColor = if (selectedFilterIndex == 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Semua")
                    }

                    Button(
                        onClick = {
                            selectedFilterIndex = 1
                            viewModel.loadStokMenipis()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedFilterIndex == 1) activeBgColor else inactiveBgColor,
                            contentColor = if (selectedFilterIndex == 1) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Stok Menipis")
                    }
                }

                if (uiState.produkList.isEmpty() && !uiState.isLoading) {
                    EmptyState(message = "Tidak ada produk terdaftar.")
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.produkList, key = { it.id }) { produk ->
                            ProdukItemCard(
                                produk = produk,
                                onClick = {
                                    navController.navigate(Screen.StokDetail.createRoute(produk.id))
                                }
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
fun ProdukItemCard(
    produk: Produk,
    onClick: () -> Unit
) {
    AppCard(onClick = onClick) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = produk.nama,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Kode: ${produk.kode} • Harga: ${produk.hargaJual.toRupiah()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            StokBadge(stok = produk.stokSaatIni, minimum = produk.minimumStok)
        }
    }
}
