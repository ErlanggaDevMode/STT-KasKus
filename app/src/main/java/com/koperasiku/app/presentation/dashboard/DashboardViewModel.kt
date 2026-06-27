package com.koperasiku.app.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koperasiku.app.core.session.SessionManager
import com.koperasiku.app.domain.usecase.kas.GetKasSummaryUseCase
import com.koperasiku.app.domain.usecase.pinjaman.GetPinjamanListUseCase
import com.koperasiku.app.domain.usecase.simpanan.GetSimpananListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val adminName: String = "Kasir / Admin",
    val saldoKas: Long = 0L,
    val totalSimpanan: Long = 0L,
    val totalPinjaman: Long = 0L,
    val isLoading: Boolean = false
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getKasSummaryUseCase: GetKasSummaryUseCase,
    private val getSimpananListUseCase: GetSimpananListUseCase,
    private val getPinjamanListUseCase: GetPinjamanListUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        val user = sessionManager.getSession()
        _uiState.update { it.copy(adminName = user?.nama ?: "Kasir / Admin", isLoading = true) }

        viewModelScope.launch {
            getKasSummaryUseCase().collect { summary ->
                _uiState.update { it.copy(saldoKas = summary.saldoTotal) }
            }
        }

        viewModelScope.launch {
            getSimpananListUseCase().collect { simpanan ->
                val total = simpanan.sumOf { it.saldo }
                _uiState.update { it.copy(totalSimpanan = total) }
            }
        }

        viewModelScope.launch {
            getPinjamanListUseCase().collect { pinjaman ->
                val total = pinjaman.filter { it.status == "AKTIF" }.sumOf { it.jumlahPinjaman }
                _uiState.update { it.copy(totalPinjaman = total, isLoading = false) }
            }
        }
    }
}
