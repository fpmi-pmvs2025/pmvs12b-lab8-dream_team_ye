package com.mockcrypto.ui.screens.details

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mockcrypto.data.repository.CoinGeckoCryptoRepository
import com.mockcrypto.data.repository.CryptoRepository
import com.mockcrypto.domain.model.CryptoCurrency
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CryptoDetailUiState(
    val isLoading: Boolean = false,
    val cryptoDetails: CryptoCurrency? = null,
    val error: String? = null
)

class CryptoDetailViewModel(
    context: Context
) : ViewModel() {
    private val repository: CryptoRepository = CoinGeckoCryptoRepository(context)
    
    private val _uiState = MutableStateFlow(CryptoDetailUiState(isLoading = false))
    val uiState: StateFlow<CryptoDetailUiState> = _uiState.asStateFlow()
    
    fun loadCryptoDetails(cryptoId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            repository.getCryptoDetails(cryptoId)
                .onSuccess { crypto ->
                    _uiState.update { it.copy(
                        isLoading = false,
                        cryptoDetails = crypto,
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
    
    /**
     * Factory for creating the CryptoDetailViewModel with a context parameter
     */
    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CryptoDetailViewModel::class.java)) {
                return CryptoDetailViewModel(context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
} 