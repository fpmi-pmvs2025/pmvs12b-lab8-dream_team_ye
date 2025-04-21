package com.mockcrypto.ui.screens.details

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

data class CryptoDetailUiState(
    val isLoading: Boolean = false,
    val cryptoCurrency: CryptoCurrency? = null,
    val error: String? = null
)

class CryptoDetailViewModel(
    private val cryptoId: String
) : ViewModel() {
    // Todo: Replace with dependency injection
    private val repository: CryptoRepository = MockCryptoRepository()
    
    private val _uiState = MutableStateFlow(CryptoDetailUiState(isLoading = true))
    val uiState: StateFlow<CryptoDetailUiState> = _uiState.asStateFlow()
    
    init {
        loadCryptoDetails()
    }
    
    fun loadCryptoDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            repository.getCryptoDetails(cryptoId)
                .onSuccess { crypto ->
                    _uiState.update { it.copy(
                        isLoading = false,
                        cryptoCurrency = crypto,
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