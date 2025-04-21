package com.mockcrypto.ui.screens.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mockcrypto.data.repository.MockCryptoRepository
import com.mockcrypto.data.repository.MockPortfolioRepository
import com.mockcrypto.data.repository.PortfolioRepository
import com.mockcrypto.domain.model.DemoAccountState
import com.mockcrypto.domain.model.PortfolioItem
import com.mockcrypto.domain.model.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PortfolioUiState(
    val isLoading: Boolean = false,
    val accountState: DemoAccountState? = null,
    val portfolioItems: List<PortfolioItem> = emptyList(),
    val transactions: List<Transaction> = emptyList(),
    val error: String? = null
)

class PortfolioViewModel : ViewModel() {
    // Todo: Replace with dependency injection
    private val repository: PortfolioRepository = MockPortfolioRepository()
    
    private val _uiState = MutableStateFlow(PortfolioUiState(isLoading = true))
    val uiState: StateFlow<PortfolioUiState> = _uiState.asStateFlow()
    
    init {
        loadPortfolioData()
    }
    
    private fun loadPortfolioData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                repository.getDemoAccountState()
                    .onSuccess { accountState ->
                        repository.getPortfolioItems()
                            .onSuccess { portfolioItems ->
                                repository.getTransactionHistory()
                                    .onSuccess { transactions ->
                                        _uiState.update { 
                                            it.copy(
                                                isLoading = false,
                                                accountState = accountState,
                                                portfolioItems = portfolioItems,
                                                transactions = transactions,
                                                error = null
                                            ) 
                                        }
                                    }
                                    .onFailure { error ->
                                        _uiState.update { 
                                            it.copy(
                                                isLoading = false,
                                                error = error.message
                                            ) 
                                        }
                                    }
                            }
                            .onFailure { error ->
                                _uiState.update { 
                                    it.copy(
                                        isLoading = false,
                                        error = error.message
                                    ) 
                                }
                            }
                    }
                    .onFailure { error ->
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                error = error.message
                            ) 
                        }
                    }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message
                    ) 
                }
            }
        }
    }
} 