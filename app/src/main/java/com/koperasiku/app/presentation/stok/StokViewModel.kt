package com.koperasiku.app.presentation.stok

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koperasiku.app.core.utils.Resource
import com.koperasiku.app.domain.model.MutasiStok
import com.koperasiku.app.domain.model.Produk
import com.koperasiku.app.domain.repository.ProdukRepository
import com.koperasiku.app.domain.usecase.produk.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class StokUiState(
    val isLoading: Boolean = false,
    val produkList: List<Produk> = emptyList(),
    val selectedProduk: Produk? = null,
    val mutasiList: List<MutasiStok> = emptyList(),
    val error: String? = null,
    val searchQuery: String = "",
    
    // Form variables
    val formKode: String = "",
    val formNama: String = "",
    val formHargaBeli: Long = 0L,
    val formHargaJual: Long = 0L,
    val formSatuan: String = "pcs",
    val formMinimumStok: Int = 5,
    val formStokAwal: Int = 0,
    val formKodeError: String? = null,
    val formNamaError: String? = null,
    
    // Stok masuk variables
    val inputStokMasukQty: Int = 0,
    val inputStokMasukKeterangan: String = "",

    // Stok opname variables
    val opnameCounts: Map<String, Int> = emptyMap(), // Key: produkId, Value: physical count
    val opnameCatatan: String = ""
)

sealed class StokEvent {
    data class ShowSnackbar(val message: String) : StokEvent()
    object NavigateBack : StokEvent()
}

@HiltViewModel
class StokViewModel @Inject constructor(
    private val getProdukListUseCase: GetProdukListUseCase,
    private val getProdukStokMenipisUseCase: GetProdukStokMenipisUseCase,
    private val createProdukUseCase: CreateProdukUseCase,
    private val updateProdukUseCase: UpdateProdukUseCase,
    private val updateStokUseCase: UpdateStokUseCase,
    private val stokOpnameUseCase: StokOpnameUseCase,
    private val repository: ProdukRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StokUiState())
    val uiState: StateFlow<StokUiState> = _uiState.asStateFlow()

    private val _events = Channel<StokEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        loadProdukList()
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchProduk(query)
    }

    private fun searchProduk(query: String) {
        viewModelScope.launch {
            if (query.isEmpty()) {
                loadProdukList()
            } else {
                repository.searchProduk(query).collect { list ->
                    _uiState.update { it.copy(produkList = list) }
                }
            }
        }
    }

    fun loadProdukList() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            getProdukListUseCase().collect { list ->
                _uiState.update { it.copy(isLoading = false, produkList = list) }
            }
        }
    }

    fun loadStokMenipis() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            getProdukStokMenipisUseCase().collect { list ->
                _uiState.update { it.copy(isLoading = false, produkList = list) }
            }
        }
    }

    fun loadProdukDetail(id: String) {
        viewModelScope.launch {
            getProdukDetailUseCase(id)
        }
    }

    private fun getProdukDetailUseCase(id: String) {
        viewModelScope.launch {
            repository.getProdukDetail(id).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                selectedProduk = resource.data,
                                formKode = resource.data.kode,
                                formNama = resource.data.nama,
                                formHargaBeli = resource.data.hargaBeli,
                                formHargaJual = resource.data.hargaJual,
                                formSatuan = resource.data.satuan,
                                formMinimumStok = resource.data.minimumStok
                            )
                        }
                        loadMutasiStok(id)
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(isLoading = false, error = resource.message) }
                        _events.send(StokEvent.ShowSnackbar(resource.message))
                    }
                }
            }
        }
    }

    private fun loadMutasiStok(produkId: String) {
        viewModelScope.launch {
            repository.getMutasiStok(produkId).collect { list ->
                _uiState.update { it.copy(mutasiList = list) }
            }
        }
    }

    fun clearForm() {
        _uiState.update {
            it.copy(
                formKode = "",
                formNama = "",
                formHargaBeli = 0L,
                formHargaJual = 0L,
                formSatuan = "pcs",
                formMinimumStok = 5,
                formStokAwal = 0,
                formKodeError = null,
                formNamaError = null
            )
        }
    }

    fun onFormKodeChanged(value: String) {
        _uiState.update { it.copy(formKode = value, formKodeError = null) }
    }

    fun onFormNamaChanged(value: String) {
        _uiState.update { it.copy(formNama = value, formNamaError = null) }
    }

    fun onFormHargaBeliChanged(value: Long) {
        _uiState.update { it.copy(formHargaBeli = value) }
    }

    fun onFormHargaJualChanged(value: Long) {
        _uiState.update { it.copy(formHargaJual = value) }
    }

    fun onFormSatuanChanged(value: String) {
        _uiState.update { it.copy(formSatuan = value) }
    }

    fun onFormMinimumStokChanged(value: Int) {
        _uiState.update { it.copy(formMinimumStok = value) }
    }

    fun onFormStokAwalChanged(value: Int) {
        _uiState.update { it.copy(formStokAwal = value) }
    }

    fun saveProduk(id: String? = null) {
        val kode = _uiState.value.formKode.trim()
        val nama = _uiState.value.formNama.trim()
        val hargaBeli = _uiState.value.formHargaBeli
        val hargaJual = _uiState.value.formHargaJual
        val satuan = _uiState.value.formSatuan.trim()
        val minimumStok = _uiState.value.formMinimumStok
        val stokAwal = _uiState.value.formStokAwal

        var hasError = false
        if (kode.isEmpty()) {
            _uiState.update { it.copy(formKodeError = "Kode produk tidak boleh kosong") }
            hasError = true
        }
        if (nama.isEmpty()) {
            _uiState.update { it.copy(formNamaError = "Nama produk tidak boleh kosong") }
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            if (id == null) {
                val newProduk = Produk(
                    id = UUID.randomUUID().toString(),
                    kode = kode,
                    nama = nama,
                    kategoriId = null,
                    hargaBeli = hargaBeli,
                    hargaJual = hargaJual,
                    satuan = satuan,
                    stokSaatIni = stokAwal,
                    minimumStok = minimumStok,
                    fotoUrl = null,
                    isAktif = true
                )
                createProdukUseCase(newProduk).collect { resource ->
                    handleSaveResource(resource)
                }
            } else {
                val existing = _uiState.value.selectedProduk ?: return@launch
                val updated = existing.copy(
                    kode = kode,
                    nama = nama,
                    hargaBeli = hargaBeli,
                    hargaJual = hargaJual,
                    satuan = satuan,
                    minimumStok = minimumStok
                )
                updateProdukUseCase(updated).collect { resource ->
                    handleSaveResource(resource)
                }
            }
        }
    }

    private suspend fun handleSaveResource(resource: Resource<Unit>) {
        when (resource) {
            is Resource.Loading -> {
                _uiState.update { it.copy(isLoading = true) }
            }
            is Resource.Success -> {
                _uiState.update { it.copy(isLoading = false) }
                _events.send(StokEvent.ShowSnackbar("Data produk berhasil disimpan"))
                _events.send(StokEvent.NavigateBack)
            }
            is Resource.Error -> {
                _uiState.update { it.copy(isLoading = false) }
                _events.send(StokEvent.ShowSnackbar(resource.message))
            }
        }
    }

    fun onInputStokQtyChanged(value: Int) {
        _uiState.update { it.copy(inputStokMasukQty = value) }
    }

    fun onInputStokKeteranganChanged(value: String) {
        _uiState.update { it.copy(inputStokMasukKeterangan = value) }
    }

    fun submitStokMasuk(produkId: String) {
        val qty = _uiState.value.inputStokMasukQty
        val ket = _uiState.value.inputStokMasukKeterangan.trim()

        if (qty <= 0) {
            viewModelScope.launch {
                _events.send(StokEvent.ShowSnackbar("Jumlah stok masuk harus lebih dari 0"))
            }
            return
        }

        viewModelScope.launch {
            updateStokUseCase(
                produkId = produkId,
                jumlah = qty,
                jenis = "MASUK",
                keterangan = if (ket.isEmpty()) "Stok Masuk Tambahan" else ket
            ).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is Resource.Success -> {
                        _uiState.update { it.copy(isLoading = false, inputStokMasukQty = 0, inputStokMasukKeterangan = "") }
                        _events.send(StokEvent.ShowSnackbar("Stok berhasil diperbarui"))
                        _events.send(StokEvent.NavigateBack)
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(isLoading = false) }
                        _events.send(StokEvent.ShowSnackbar(resource.message))
                    }
                }
            }
        }
    }

    // Stok opname logic
    fun initializeOpname(products: List<Produk>) {
        val counts = products.associate { it.id to it.stokSaatIni }
        _uiState.update { it.copy(opnameCounts = counts, opnameCatatan = "") }
    }

    fun onOpnameCountChanged(produkId: String, count: Int) {
        val currentCounts = _uiState.value.opnameCounts.toMutableMap()
        currentCounts[produkId] = count
        _uiState.update { it.copy(opnameCounts = currentCounts) }
    }

    fun onOpnameCatatanChanged(value: String) {
        _uiState.update { it.copy(opnameCatatan = value) }
    }

    fun submitStokOpname() {
        val counts = _uiState.value.opnameCounts
        val catatan = _uiState.value.opnameCatatan

        viewModelScope.launch {
            stokOpnameUseCase(counts, catatan).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is Resource.Success -> {
                        _uiState.update { it.copy(isLoading = false, opnameCounts = emptyMap(), opnameCatatan = "") }
                        _events.send(StokEvent.ShowSnackbar("Stok Opname berhasil diproses"))
                        _events.send(StokEvent.NavigateBack)
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(isLoading = false) }
                        _events.send(StokEvent.ShowSnackbar(resource.message))
                    }
                }
            }
        }
    }
}
