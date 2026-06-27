package com.koperasiku.app.presentation.pos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koperasiku.app.core.session.SessionManager
import com.koperasiku.app.core.utils.Resource
import com.koperasiku.app.domain.model.CartItem
import com.koperasiku.app.domain.model.Anggota
import com.koperasiku.app.domain.model.Produk
import com.koperasiku.app.domain.model.Transaksi
import com.koperasiku.app.domain.model.TransaksiItem
import com.koperasiku.app.domain.usecase.transaksi.*
import com.koperasiku.app.domain.usecase.anggota.GetAnggotaListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import java.util.UUID
import javax.inject.Inject

data class PosUiState(
    val isLoading: Boolean = false,
    val cart: List<CartItem> = emptyList(),
    val produkList: List<Produk> = emptyList(),
    val anggotaList: List<Anggota> = emptyList(),
    val selectedAnggota: Anggota? = null,
    val searchQuery: String = "",
    val nikSearchQuery: String = "",
    val nominalBayar: Long = 0L,
    val metodePembayaran: String = "CASH", // CASH, DEPOSIT
    val historyList: List<Transaksi> = emptyList(),
    val lastCompletedTransaksi: Transaksi? = null,
    val error: String? = null
) {
    val totalBelanja: Long get() = cart.sumOf { it.subtotal }
    val nominalKembali: Long get() = if (nominalBayar >= totalBelanja) nominalBayar - totalBelanja else 0L
}

sealed class PosEvent {
    data class ShowSnackbar(val message: String) : PosEvent()
    data class CheckoutSuccess(val transaksi: Transaksi) : PosEvent()
    object NavigateBack : PosEvent()
}

@HiltViewModel
class PosViewModel @Inject constructor(
    private val processCheckoutUseCase: ProcessCheckoutUseCase,
    private val getTransaksiHistoryUseCase: GetTransaksiHistoryUseCase,
    private val searchProdukPosUseCase: SearchProdukPosUseCase,
    private val getAnggotaListUseCase: GetAnggotaListUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(PosUiState())
    val uiState: StateFlow<PosUiState> = _uiState.asStateFlow()

    private val _events = Channel<PosEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        loadInitialData()
    }

    fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // Fetch all active products
            searchProdukPosUseCase("").collect { list ->
                _uiState.update { it.copy(produkList = list, isLoading = false) }
            }
        }
        viewModelScope.launch {
            // Fetch members list for lookups
            getAnggotaListUseCase().collect { list ->
                _uiState.update { it.copy(anggotaList = list) }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        viewModelScope.launch {
            searchProdukPosUseCase(query).collect { list ->
                _uiState.update { it.copy(produkList = list) }
            }
        }
    }

    fun onNikSearchQueryChanged(query: String) {
        _uiState.update { it.copy(nikSearchQuery = query) }
        if (query.isEmpty()) {
            _uiState.update { it.copy(selectedAnggota = null) }
        } else {
            val matched = _uiState.value.anggotaList.firstOrNull { 
                it.nik == query || it.nomorAnggota.equals(query, ignoreCase = true)
            }
            _uiState.update { it.copy(selectedAnggota = matched) }
        }
    }

    fun selectAnggota(anggota: Anggota?) {
        _uiState.update { 
            it.copy(
                selectedAnggota = anggota,
                nikSearchQuery = anggota?.nik ?: ""
            )
        }
    }

    fun addToCart(produk: Produk) {
        if (produk.stokSaatIni <= 0) {
            viewModelScope.launch {
                _events.send(PosEvent.ShowSnackbar("Stok produk habis!"))
            }
            return
        }

        val currentCart = _uiState.value.cart.toMutableList()
        val index = currentCart.indexOfFirst { it.produk.id == produk.id }

        if (index != -1) {
            val item = currentCart[index]
            if (item.kuantitas + 1 > produk.stokSaatIni) {
                viewModelScope.launch {
                    _events.send(PosEvent.ShowSnackbar("Jumlah melebihi stok tersedia!"))
                }
                return
            }
            currentCart[index] = item.copy(kuantitas = item.kuantitas + 1)
        } else {
            currentCart.add(CartItem(produk, 1))
        }

        _uiState.update { it.copy(cart = currentCart) }
    }

    fun removeFromCart(produk: Produk) {
        val currentCart = _uiState.value.cart.toMutableList()
        val index = currentCart.indexOfFirst { it.produk.id == produk.id }

        if (index != -1) {
            val item = currentCart[index]
            if (item.kuantitas > 1) {
                currentCart[index] = item.copy(kuantitas = item.kuantitas - 1)
            } else {
                currentCart.removeAt(index)
            }
        }
        _uiState.update { it.copy(cart = currentCart) }
    }

    fun clearCart() {
        _uiState.update { 
            it.copy(
                cart = emptyList(),
                selectedAnggota = null,
                nikSearchQuery = "",
                nominalBayar = 0L
            )
        }
    }

    fun onNominalBayarChanged(value: Long) {
        _uiState.update { it.copy(nominalBayar = value) }
    }

    fun onMetodePembayaranChanged(value: String) {
        _uiState.update { it.copy(metodePembayaran = value) }
    }

    fun checkout() {
        val cart = _uiState.value.cart
        val total = _uiState.value.totalBelanja
        val bayar = _uiState.value.nominalBayar
        val kembali = _uiState.value.nominalKembali
        val method = _uiState.value.metodePembayaran
        val kasirId = sessionManager.currentUserId ?: "SYSTEM"

        if (cart.isEmpty()) {
            viewModelScope.launch {
                _events.send(PosEvent.ShowSnackbar("Keranjang belanja kosong"))
            }
            return
        }

        if (bayar < total) {
            viewModelScope.launch {
                _events.send(PosEvent.ShowSnackbar("Nominal pembayaran kurang!"))
            }
            return
        }

        viewModelScope.launch {
            val transaksiId = UUID.randomUUID().toString()
            val noTransaksi = "TRX-${Clock.System.now().toEpochMilliseconds().toString().takeLast(8)}"
            
            val transaksiItems = cart.map { item ->
                TransaksiItem(
                    id = UUID.randomUUID().toString(),
                    transaksiId = transaksiId,
                    produkId = item.produk.id,
                    kuantitas = item.kuantitas,
                    hargaSatuan = item.produk.hargaJual,
                    subtotal = item.subtotal,
                    produkNama = item.produk.nama
                )
            }

            val transaksi = Transaksi(
                id = transaksiId,
                nomorTransaksi = noTransaksi,
                anggotaId = _uiState.value.selectedAnggota?.id,
                userId = kasirId,
                totalBelanja = total,
                nominalBayar = bayar,
                nominalKembali = kembali,
                jenisPembayaran = method,
                keterangan = "Pembelian POS oleh Anggota ${_uiState.value.selectedAnggota?.nama ?: "Guest"}",
                createdAt = Clock.System.now().toString(),
                items = transaksiItems
            )

            processCheckoutUseCase(transaksi).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is Resource.Success -> {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                lastCompletedTransaksi = transaksi
                            )
                        }
                        _events.send(PosEvent.CheckoutSuccess(transaksi))
                        clearCart()
                        loadInitialData() // refresh stocks
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(isLoading = false) }
                        _events.send(PosEvent.ShowSnackbar(resource.message))
                    }
                }
            }
        }
    }

    fun loadHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getTransaksiHistoryUseCase().collect { list ->
                _uiState.update { it.copy(historyList = list, isLoading = false) }
            }
        }
    }
}
