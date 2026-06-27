package com.koperasiku.app.presentation.kas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koperasiku.app.core.session.SessionManager
import com.koperasiku.app.core.utils.Resource
import com.koperasiku.app.domain.model.Kas
import com.koperasiku.app.domain.model.KasSummary
import com.koperasiku.app.domain.usecase.kas.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.UUID
import javax.inject.Inject

data class KasUiState(
    val isLoading: Boolean = false,
    val kasList: List<Kas> = emptyList(),
    val filteredKasList: List<Kas> = emptyList(),
    val summary: KasSummary = KasSummary(0L, 0L, 0L),
    val selectedFilter: String = "SEMUA",
    val error: String? = null,

    // Form states
    val formJenis: String = "MASUK",
    val formKategori: String = "PENDAPATAN",
    val formJumlah: Long = 0L,
    val formKeterangan: String = "",
    val formJumlahError: String? = null
)

sealed class KasEvent {
    data class ShowSnackbar(val message: String) : KasEvent()
    object NavigateBack : KasEvent()
}

@HiltViewModel
class KasViewModel @Inject constructor(
    private val getKasListUseCase: GetKasListUseCase,
    private val getKasSummaryUseCase: GetKasSummaryUseCase,
    private val createKasTransactionUseCase: CreateKasTransactionUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(KasUiState())
    val uiState: StateFlow<KasUiState> = _uiState.asStateFlow()

    private val _events = Channel<KasEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        loadSummary()
        loadKasList()
    }

    fun loadSummary() {
        viewModelScope.launch {
            getKasSummaryUseCase().collect { summary ->
                _uiState.update { it.copy(summary = summary) }
            }
        }
    }

    fun loadKasList() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            getKasListUseCase().collect { list ->
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        kasList = list,
                        filteredKasList = filterList(list, _uiState.value.selectedFilter)
                    )
                }
            }
        }
    }

    fun onFilterChanged(filter: String) {
        _uiState.update { 
            it.copy(
                selectedFilter = filter,
                filteredKasList = filterList(it.kasList, filter)
            )
        }
    }

    private fun filterList(list: List<Kas>, filter: String): List<Kas> {
        return when (filter) {
            "MASUK" -> list.filter { it.jenis == "MASUK" }
            "KELUAR" -> list.filter { it.jenis == "KELUAR" }
            else -> list
        }
    }

    fun clearForm() {
        _uiState.update {
            it.copy(
                formJenis = "MASUK",
                formKategori = "PENDAPATAN",
                formJumlah = 0L,
                formKeterangan = "",
                formJumlahError = null
            )
        }
    }

    fun onFormJenisChanged(value: String) {
        // Automatically align category based on type
        val defaultKategori = if (value == "MASUK") "PENDAPATAN" else "PENGELUARAN"
        _uiState.update { 
            it.copy(
                formJenis = value,
                formKategori = defaultKategori
            )
        }
    }

    fun onFormKategoriChanged(value: String) {
        _uiState.update { it.copy(formKategori = value) }
    }

    fun onFormJumlahChanged(value: Long) {
        _uiState.update { it.copy(formJumlah = value, formJumlahError = null) }
    }

    fun onFormKeteranganChanged(value: String) {
        _uiState.update { it.copy(formKeterangan = value) }
    }

    fun saveKasTransaction() {
        val jenis = _uiState.value.formJenis
        val kategori = _uiState.value.formKategori
        val jumlah = _uiState.value.formJumlah
        val keterangan = _uiState.value.formKeterangan.trim()
        val kasirId = sessionManager.currentUserId ?: "SYSTEM"

        if (jumlah <= 0L) {
            _uiState.update { it.copy(formJumlahError = "Jumlah harus lebih besar dari 0") }
            return
        }

        viewModelScope.launch {
            val systemTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            val dateNow = systemTime.date.toString() // YYYY-MM-DD

            val kas = Kas(
                id = UUID.randomUUID().toString(),
                jenis = jenis,
                kategoriId = kategori,
                jumlah = jumlah,
                keterangan = if (keterangan.isEmpty()) "Transaksi Manual $jenis" else keterangan,
                referensiId = null,
                referensiTipe = "MANUAL",
                userId = kasirId,
                tanggal = dateNow,
                createdAt = Clock.System.now().toString()
            )

            createKasTransactionUseCase(kas).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is Resource.Success -> {
                        _uiState.update { it.copy(isLoading = false) }
                        _events.send(KasEvent.ShowSnackbar("Transaksi kas berhasil disimpan"))
                        _events.send(KasEvent.NavigateBack)
                        loadSummary() // refresh summary
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(isLoading = false) }
                        _events.send(KasEvent.ShowSnackbar(resource.message))
                    }
                }
            }
        }
    }
}
