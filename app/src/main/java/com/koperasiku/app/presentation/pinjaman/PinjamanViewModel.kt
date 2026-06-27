package com.koperasiku.app.presentation.pinjaman

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koperasiku.app.core.session.SessionManager
import com.koperasiku.app.core.utils.Resource
import com.koperasiku.app.domain.model.Angsuran
import com.koperasiku.app.domain.model.Anggota
import com.koperasiku.app.domain.model.Pinjaman
import com.koperasiku.app.domain.usecase.anggota.GetAnggotaListUseCase
import com.koperasiku.app.domain.usecase.pinjaman.*
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

data class PinjamanUiState(
    val isLoading: Boolean = false,
    val pinjamanList: List<Pinjaman> = emptyList(),
    val filteredList: List<Pinjaman> = emptyList(),
    val selectedPinjaman: Pinjaman? = null,
    val angsuranList: List<Angsuran> = emptyList(),
    val anggotaList: List<Anggota> = emptyList(),
    val selectedFilter: String = "SEMUA",
    val error: String? = null,

    // Loan Form states
    val formAnggotaId: String = "",
    val formAnggotaNama: String = "",
    val formJumlah: Long = 0L,
    val formTenor: Int = 12,
    val formBunga: Double = 1.0,
    val formKeperluan: String = "",
    val formJumlahError: String? = null,

    // Bayar Angsuran Form
    val selectedAngsuran: Angsuran? = null,
    val formDenda: Long = 0L
) {
    // Simulation computed props
    val simulasiPokok: Long get() = if (formTenor > 0) formJumlah / formTenor else 0L
    val simulasiBunga: Long get() = (formJumlah * formBunga / 100).toLong()
    val simulasiPerBulan: Long get() = simulasiPokok + simulasiBunga
    val simulasiTotalBayar: Long get() = simulasiPerBulan * formTenor
    val simulasiTotalBunga: Long get() = simulasiBunga * formTenor

    // Summary
    val totalPinjamanAktif: Long get() = pinjamanList.filter { it.status == "AKTIF" }.sumOf { it.jumlahPinjaman }
    val totalTunggakan: Long get() = pinjamanList.filter { it.status == "AKTIF" }.sumOf { it.angsuranPerBulan }
}

sealed class PinjamanEvent {
    data class ShowSnackbar(val message: String) : PinjamanEvent()
    object NavigateBack : PinjamanEvent()
}

@HiltViewModel
class PinjamanViewModel @Inject constructor(
    private val getPinjamanListUseCase: GetPinjamanListUseCase,
    private val getPinjamanDetailUseCase: GetPinjamanDetailUseCase,
    private val applyPinjamanUseCase: ApplyPinjamanUseCase,
    private val approvePinjamanUseCase: ApprovePinjamanUseCase,
    private val rejectPinjamanUseCase: RejectPinjamanUseCase,
    private val payAngsuranUseCase: PayAngsuranUseCase,
    private val getAngsuranListUseCase: GetAngsuranListUseCase,
    private val getAnggotaListUseCase: GetAnggotaListUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(PinjamanUiState())
    val uiState: StateFlow<PinjamanUiState> = _uiState.asStateFlow()

    private val _events = Channel<PinjamanEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        loadPinjamanList()
        loadAnggotaList()
    }

    fun loadPinjamanList() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            getPinjamanListUseCase().collect { list ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        pinjamanList = list,
                        filteredList = filterList(list, it.selectedFilter)
                    )
                }
            }
        }
    }

    private fun loadAnggotaList() {
        viewModelScope.launch {
            getAnggotaListUseCase().collect { list ->
                _uiState.update { it.copy(anggotaList = list) }
            }
        }
    }

    fun loadPinjamanDetail(pinjamanId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getPinjamanDetailUseCase(pinjamanId).collect { pinjaman ->
                _uiState.update { it.copy(selectedPinjaman = pinjaman, isLoading = false) }
            }
        }
        viewModelScope.launch {
            getAngsuranListUseCase(pinjamanId).collect { list ->
                _uiState.update { it.copy(angsuranList = list) }
            }
        }
    }

    fun onFilterChanged(filter: String) {
        _uiState.update {
            it.copy(
                selectedFilter = filter,
                filteredList = filterList(it.pinjamanList, filter)
            )
        }
    }

    private fun filterList(list: List<Pinjaman>, filter: String): List<Pinjaman> {
        return if (filter == "SEMUA") list else list.filter { it.status == filter }
    }

    // Form helpers
    fun clearForm() {
        _uiState.update {
            it.copy(
                formAnggotaId = "",
                formAnggotaNama = "",
                formJumlah = 0L,
                formTenor = 12,
                formBunga = 1.0,
                formKeperluan = "",
                formJumlahError = null
            )
        }
    }

    fun onFormAnggotaSelected(anggota: Anggota) {
        _uiState.update { it.copy(formAnggotaId = anggota.id, formAnggotaNama = anggota.nama) }
    }

    fun onFormJumlahChanged(v: Long) {
        _uiState.update { it.copy(formJumlah = v, formJumlahError = null) }
    }

    fun onFormTenorChanged(v: Int) {
        _uiState.update { it.copy(formTenor = v) }
    }

    fun onFormKeperluanChanged(v: String) {
        _uiState.update { it.copy(formKeperluan = v) }
    }

    fun onFormDendaChanged(v: Long) {
        _uiState.update { it.copy(formDenda = v) }
    }

    fun setSelectedAngsuran(angsuran: Angsuran) {
        _uiState.update { it.copy(selectedAngsuran = angsuran, formDenda = 0L) }
    }

    fun submitPengajuan() {
        val anggotaId = _uiState.value.formAnggotaId
        val jumlah = _uiState.value.formJumlah
        if (anggotaId.isEmpty()) {
            viewModelScope.launch {
                _events.send(PinjamanEvent.ShowSnackbar("Pilih anggota pemohon terlebih dahulu."))
            }
            return
        }
        if (jumlah <= 0L) {
            _uiState.update { it.copy(formJumlahError = "Jumlah pinjaman harus lebih besar dari 0") }
            return
        }
        viewModelScope.launch {
            val now = Clock.System.now()
            val dateNow = now.toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
            val nomorPinjaman = "PJM-${System.currentTimeMillis()}"
            val pinjaman = Pinjaman(
                id = UUID.randomUUID().toString(),
                nomorPinjaman = nomorPinjaman,
                anggotaId = anggotaId,
                jumlahPinjaman = jumlah,
                tenorBulan = _uiState.value.formTenor,
                bungaPersenPerBulan = _uiState.value.formBunga,
                keperluan = _uiState.value.formKeperluan.trim().ifEmpty { null },
                status = "DIAJUKAN",
                tanggalPengajuan = dateNow,
                tanggalDisetujui = null,
                tanggalLunasTarget = null,
                approvedByUserId = null,
                createdAt = now.toString()
            )
            applyPinjamanUseCase(pinjaman).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is Resource.Success -> {
                        _uiState.update { it.copy(isLoading = false) }
                        _events.send(PinjamanEvent.ShowSnackbar("Pengajuan pinjaman berhasil disimpan"))
                        _events.send(PinjamanEvent.NavigateBack)
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(isLoading = false) }
                        _events.send(PinjamanEvent.ShowSnackbar(resource.message))
                    }
                }
            }
        }
    }

    fun approvePinjaman(pinjamanId: String) {
        val approverId = sessionManager.currentUserId ?: return
        viewModelScope.launch {
            approvePinjamanUseCase(pinjamanId, approverId).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is Resource.Success -> {
                        _uiState.update { it.copy(isLoading = false) }
                        _events.send(PinjamanEvent.ShowSnackbar("Pinjaman berhasil disetujui dan dicairkan"))
                        loadPinjamanDetail(pinjamanId)
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(isLoading = false) }
                        _events.send(PinjamanEvent.ShowSnackbar(resource.message))
                    }
                }
            }
        }
    }

    fun rejectPinjaman(pinjamanId: String) {
        viewModelScope.launch {
            rejectPinjamanUseCase(pinjamanId).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is Resource.Success -> {
                        _uiState.update { it.copy(isLoading = false) }
                        _events.send(PinjamanEvent.ShowSnackbar("Pengajuan pinjaman ditolak"))
                        _events.send(PinjamanEvent.NavigateBack)
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(isLoading = false) }
                        _events.send(PinjamanEvent.ShowSnackbar(resource.message))
                    }
                }
            }
        }
    }

    fun bayarAngsuran(angsuranId: String) {
        val denda = _uiState.value.formDenda
        viewModelScope.launch {
            payAngsuranUseCase(angsuranId, denda).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is Resource.Success -> {
                        _uiState.update { it.copy(isLoading = false) }
                        _events.send(PinjamanEvent.ShowSnackbar("Pembayaran angsuran berhasil"))
                        _events.send(PinjamanEvent.NavigateBack)
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(isLoading = false) }
                        _events.send(PinjamanEvent.ShowSnackbar(resource.message))
                    }
                }
            }
        }
    }
}
