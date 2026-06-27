package com.koperasiku.app.presentation.simpanan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koperasiku.app.core.utils.Resource
import com.koperasiku.app.domain.model.Anggota
import com.koperasiku.app.domain.model.MutasiSimpanan
import com.koperasiku.app.domain.model.Simpanan
import com.koperasiku.app.domain.repository.SimpananRepository
import com.koperasiku.app.domain.usecase.anggota.GetAnggotaListUseCase
import com.koperasiku.app.domain.usecase.simpanan.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SimpananUiState(
    val isLoading: Boolean = false,
    val simpananList: List<Simpanan> = emptyList(),
    val anggotaList: List<Anggota> = emptyList(),
    val filteredAnggotaList: List<Anggota> = emptyList(),
    val selectedAnggota: Anggota? = null,
    val selectedSimpananDetails: List<Simpanan> = emptyList(),
    val mutasiList: List<MutasiSimpanan> = emptyList(),
    val searchQuery: String = "",
    val error: String? = null,

    // Form inputs
    val formJenisTrx: String = "SETORAN",
    val formJenisSimpanan: String = "POKOK",
    val formJumlah: Long = 0L,
    val formKeterangan: String = "",
    val formJumlahError: String? = null
) {
    val totalSimpanan: Long get() = selectedSimpananDetails.sumOf { it.saldo }
}

sealed class SimpananEvent {
    data class ShowSnackbar(val message: String) : SimpananEvent()
    object NavigateBack : SimpananEvent()
}

@HiltViewModel
class SimpananViewModel @Inject constructor(
    private val getSimpananListUseCase: GetSimpananListUseCase,
    private val getSimpananDetailUseCase: GetSimpananDetailUseCase,
    private val depositSimpananUseCase: DepositSimpananUseCase,
    private val withdrawSimpananUseCase: WithdrawSimpananUseCase,
    private val getAnggotaListUseCase: GetAnggotaListUseCase,
    private val repository: SimpananRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SimpananUiState())
    val uiState: StateFlow<SimpananUiState> = _uiState.asStateFlow()

    private val _events = Channel<SimpananEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        loadInitialData()
    }

    fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            getAnggotaListUseCase().collect { list ->
                _uiState.update { 
                    it.copy(
                        anggotaList = list,
                        filteredAnggotaList = list,
                        isLoading = false
                    ) 
                }
            }
        }
        viewModelScope.launch {
            getSimpananListUseCase().collect { list ->
                _uiState.update { it.copy(simpananList = list) }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        val filtered = if (query.isEmpty()) {
            _uiState.value.anggotaList
        } else {
            _uiState.value.anggotaList.filter { 
                it.nama.contains(query, ignoreCase = true) || it.nomorAnggota.contains(query, ignoreCase = true)
            }
        }
        _uiState.update { it.copy(filteredAnggotaList = filtered) }
    }

    fun loadSimpananDetail(anggotaId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val matchedAnggota = _uiState.value.anggotaList.firstOrNull { it.id == anggotaId }
            
            getSimpananDetailUseCase(anggotaId).collect { details ->
                _uiState.update { 
                    it.copy(
                        selectedAnggota = matchedAnggota,
                        selectedSimpananDetails = details,
                        isLoading = false
                    ) 
                }
            }
        }
        viewModelScope.launch {
            repository.getMutasiSimpanan(anggotaId).collect { list ->
                _uiState.update { it.copy(mutasiList = list) }
            }
        }
    }

    fun clearForm() {
        _uiState.update {
            it.copy(
                formJenisTrx = "SETORAN",
                formJenisSimpanan = "POKOK",
                formJumlah = 0L,
                formKeterangan = "",
                formJumlahError = null
            )
        }
    }

    fun onFormJenisTrxChanged(value: String) {
        _uiState.update { it.copy(formJenisTrx = value) }
    }

    fun onFormJenisSimpananChanged(value: String) {
        _uiState.update { it.copy(formJenisSimpanan = value) }
    }

    fun onFormJumlahChanged(value: Long) {
        _uiState.update { it.copy(formJumlah = value, formJumlahError = null) }
    }

    fun onFormKeteranganChanged(value: String) {
        _uiState.update { it.copy(formKeterangan = value) }
    }

    fun submitSimpananTransaction(anggotaId: String) {
        val trxType = _uiState.value.formJenisTrx
        val savingType = _uiState.value.formJenisSimpanan
        val qty = _uiState.value.formJumlah
        val ket = _uiState.value.formKeterangan.trim()

        if (qty <= 0L) {
            _uiState.update { it.copy(formJumlahError = "Jumlah harus lebih besar dari 0") }
            return
        }

        viewModelScope.launch {
            val flow = if (trxType == "SETORAN") {
                depositSimpananUseCase(anggotaId, savingType, qty, if (ket.isEmpty()) null else ket)
            } else {
                withdrawSimpananUseCase(anggotaId, savingType, qty, if (ket.isEmpty()) null else ket)
            }

            flow.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is Resource.Success -> {
                        _uiState.update { it.copy(isLoading = false) }
                        _events.send(SimpananEvent.ShowSnackbar("Transaksi simpanan berhasil diproses"))
                        _events.send(SimpananEvent.NavigateBack)
                        loadSimpananDetail(anggotaId) // refresh page details
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(isLoading = false) }
                        _events.send(SimpananEvent.ShowSnackbar(resource.message))
                    }
                }
            }
        }
    }
}
