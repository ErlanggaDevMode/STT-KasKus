package com.koperasiku.app.presentation.anggota

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koperasiku.app.core.utils.Resource
import com.koperasiku.app.domain.model.Anggota
import com.koperasiku.app.domain.repository.AnggotaRepository
import com.koperasiku.app.domain.usecase.anggota.*
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

data class AnggotaUiState(
    val isLoading: Boolean = false,
    val anggotaList: List<Anggota> = emptyList(),
    val selectedAnggota: Anggota? = null,
    val error: String? = null,
    val searchQuery: String = "",
    
    // Form states
    val formNama: String = "",
    val formNik: String = "",
    val formAlamat: String = "",
    val formNoHp: String = "",
    val formNamaError: String? = null,
    val formNikError: String? = null,
    val formNoHpError: String? = null,
    val isFormSubmitSuccess: Boolean = false
)

sealed class AnggotaEvent {
    data class ShowSnackbar(val message: String) : AnggotaEvent()
    object NavigateBack : AnggotaEvent()
}

@HiltViewModel
class AnggotaViewModel @Inject constructor(
    private val getAnggotaListUseCase: GetAnggotaListUseCase,
    private val getAnggotaDetailUseCase: GetAnggotaDetailUseCase,
    private val createAnggotaUseCase: CreateAnggotaUseCase,
    private val updateAnggotaUseCase: UpdateAnggotaUseCase,
    private val deactivateAnggotaUseCase: DeactivateAnggotaUseCase,
    private val repository: AnggotaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnggotaUiState())
    val uiState: StateFlow<AnggotaUiState> = _uiState.asStateFlow()

    private val _events = Channel<AnggotaEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        loadAnggotaList()
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchAnggota(query)
    }

    private fun searchAnggota(query: String) {
        viewModelScope.launch {
            if (query.isEmpty()) {
                loadAnggotaList()
            } else {
                repository.searchAnggota(query).collect { list ->
                    _uiState.update { it.copy(anggotaList = list) }
                }
            }
        }
    }

    fun loadAnggotaList() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            getAnggotaListUseCase().collect { list ->
                _uiState.update { it.copy(isLoading = false, anggotaList = list) }
            }
        }
    }

    fun loadAnggotaDetail(id: String) {
        viewModelScope.launch {
            getAnggotaDetailUseCase(id).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                selectedAnggota = resource.data,
                                formNama = resource.data.nama,
                                formNik = resource.data.nik,
                                formAlamat = resource.data.alamat ?: "",
                                formNoHp = resource.data.noHp ?: ""
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(isLoading = false, error = resource.message) }
                        _events.send(AnggotaEvent.ShowSnackbar(resource.message))
                    }
                }
            }
        }
    }

    fun clearForm() {
        _uiState.update {
            it.copy(
                formNama = "",
                formNik = "",
                formAlamat = "",
                formNoHp = "",
                formNamaError = null,
                formNikError = null,
                formNoHpError = null,
                isFormSubmitSuccess = false
            )
        }
    }

    fun onFormNamaChanged(value: String) {
        _uiState.update { it.copy(formNama = value, formNamaError = null) }
    }

    fun onFormNikChanged(value: String) {
        _uiState.update { it.copy(formNik = value, formNikError = null) }
    }

    fun onFormAlamatChanged(value: String) {
        _uiState.update { it.copy(formAlamat = value) }
    }

    fun onFormNoHpChanged(value: String) {
        _uiState.update { it.copy(formNoHp = value, formNoHpError = null) }
    }

    fun saveAnggota(id: String? = null) {
        val nama = _uiState.value.formNama.trim()
        val nik = _uiState.value.formNik.trim()
        val alamat = _uiState.value.formAlamat.trim()
        val noHp = _uiState.value.formNoHp.trim()

        var hasError = false
        if (nama.isEmpty()) {
            _uiState.update { it.copy(formNamaError = "Nama tidak boleh kosong") }
            hasError = true
        }
        if (nik.isEmpty()) {
            _uiState.update { it.copy(formNikError = "NIK tidak boleh kosong") }
            hasError = true
        } else if (nik.length < 16) {
            _uiState.update { it.copy(formNikError = "NIK harus 16 digit") }
            hasError = true
        }
        if (noHp.isEmpty()) {
            _uiState.update { it.copy(formNoHpError = "Nomor HP tidak boleh kosong") }
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            val systemTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            val dateNow = systemTime.date

            if (id == null) {
                val newAnggota = Anggota(
                    id = UUID.randomUUID().toString(),
                    nomorAnggota = "KOP-TEMP-${UUID.randomUUID().toString().take(6).uppercase()}",
                    nama = nama,
                    nik = nik,
                    alamat = if (alamat.isEmpty()) null else alamat,
                    noHp = noHp,
                    fotoKtpUrl = null,
                    isAktif = true,
                    tanggalGabung = dateNow
                )
                createAnggotaUseCase(newAnggota).collect { resource ->
                    handleSaveResource(resource)
                }
            } else {
                val existing = _uiState.value.selectedAnggota ?: return@launch
                val updated = existing.copy(
                    nama = nama,
                    nik = nik,
                    alamat = if (alamat.isEmpty()) null else alamat,
                    noHp = noHp
                )
                updateAnggotaUseCase(updated).collect { resource ->
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
                _uiState.update { it.copy(isLoading = false, isFormSubmitSuccess = true) }
                _events.send(AnggotaEvent.ShowSnackbar("Data anggota berhasil disimpan"))
                _events.send(AnggotaEvent.NavigateBack)
            }
            is Resource.Error -> {
                _uiState.update { it.copy(isLoading = false) }
                _events.send(AnggotaEvent.ShowSnackbar(resource.message))
            }
        }
    }

    fun deactivateAnggota(id: String) {
        viewModelScope.launch {
            deactivateAnggotaUseCase(id).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is Resource.Success -> {
                        _uiState.update { it.copy(isLoading = false) }
                        _events.send(AnggotaEvent.ShowSnackbar("Anggota berhasil dinonaktifkan"))
                        _events.send(AnggotaEvent.NavigateBack)
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(isLoading = false) }
                        _events.send(AnggotaEvent.ShowSnackbar(resource.message))
                    }
                }
            }
        }
    }
}
