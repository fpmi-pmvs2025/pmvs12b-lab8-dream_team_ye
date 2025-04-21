package com.mockcrypto.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mockcrypto.data.repository.CryptoRepository
import com.mockcrypto.data.repository.MockCryptoRepository
import com.mockcrypto.domain.model.CryptoCurrency
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DashboardUiState(
    val isLoading: Boolean = false,
    val cryptoList: List<CryptoCurrency> = emptyList(),
    val error: String? = null
)

class DashboardViewModel : ViewModel() {
    // ToDo: Replace with dependency injection
    private val repository: CryptoRepository = MockCryptoRepository()
    
    private val _uiState = MutableStateFlow(DashboardUiState(isLoading = true))
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()
    
    init {
        loadCryptoList()
    }
    
    fun loadCryptoList() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            repository.getCryptoList()
                .onSuccess { cryptos ->
                    _uiState.update { it.copy(
                        isLoading = false,
                        cryptoList = cryptos,
                        error = null
                    ) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(
                        isLoading = false,
                        error = error.message
                    ) }
                }
        }
    }
} 