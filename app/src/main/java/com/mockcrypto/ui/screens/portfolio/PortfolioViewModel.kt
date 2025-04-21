package com.mockcrypto.ui.screens.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mockcrypto.data.repository.MockPortfolioRepository
import com.mockcrypto.domain.model.DemoAccountState
import com.mockcrypto.domain.model.PortfolioItem
import com.mockcrypto.domain.model.Transaction
import com.mockcrypto.domain.model.TransactionType
import com.mockcrypto.domain.usecase.ExecuteTradeUseCase
import com.mockcrypto.domain.usecase.GetDemoAccountStateUseCase
import com.mockcrypto.domain.usecase.GetPortfolioItemsUseCase
import com.mockcrypto.domain.usecase.GetTransactionHistoryUseCase
import com.mockcrypto.domain.usecase.PortfolioUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal

data class PortfolioUiState(
    val isLoading: Boolean = false,
    val accountState: DemoAccountState? = null,
    val portfolioItems: List<PortfolioItem> = emptyList(),
    val transactions: List<Transaction> = emptyList(),
    val error: String? = null,
    val operationSuccess: Boolean? = null, // Null means no operation, true means success, false means failure
    val operationMessage: String? = null
)

class PortfolioViewModel : ViewModel() {
    // Todo: Replace with dependency injection
    private val repository = MockPortfolioRepository()
    private val portfolioUseCases = PortfolioUseCases(
        getDemoAccountState = GetDemoAccountStateUseCase(repository),
        getPortfolioItems = GetPortfolioItemsUseCase(repository),
        getTransactionHistory = GetTransactionHistoryUseCase(repository),
        executeTrade = ExecuteTradeUseCase(repository)
    )
    
    private val _uiState = MutableStateFlow(PortfolioUiState(isLoading = true))
    val uiState: StateFlow<PortfolioUiState> = _uiState.asStateFlow()
    
    init {
        loadPortfolioData()
    }
    
    fun loadPortfolioData() {
        viewModelScope.launch {
            _uiState.update { it.copy(
                isLoading = true, 
                error = null,
                operationSuccess = null,
                operationMessage = null
            ) }
            
            try {
                // Use the use cases to get the data
                val accountStateResult = portfolioUseCases.getDemoAccountState()
                val portfolioItemsResult = portfolioUseCases.getPortfolioItems()
                val transactionsResult = portfolioUseCases.getTransactionHistory()
                
                // Check for errors
                when {
                    accountStateResult.isFailure -> {
                        _uiState.update { it.copy(
                            isLoading = false,
                            error = accountStateResult.exceptionOrNull()?.message
                        ) }
                        return@launch
                    }
                    
                    portfolioItemsResult.isFailure -> {
                        _uiState.update { it.copy(
                            isLoading = false,
                            error = portfolioItemsResult.exceptionOrNull()?.message
                        ) }
                        return@launch
                    }
                    
                    transactionsResult.isFailure -> {
                        _uiState.update { it.copy(
                            isLoading = false,
                            error = transactionsResult.exceptionOrNull()?.message
                        ) }
                        return@launch
                    }
                    
                    else -> {
                        _uiState.update { it.copy(
                            isLoading = false,
                            accountState = accountStateResult.getOrNull(),
                            portfolioItems = portfolioItemsResult.getOrNull() ?: emptyList(),
                            transactions = transactionsResult.getOrNull() ?: emptyList(),
                            error = null
                        ) }
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
    
    fun executeTrade(
        cryptoId: String,
        symbol: String,
        name: String,
        amount: BigDecimal,
        price: BigDecimal,
        type: TransactionType
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, operationSuccess = null, operationMessage = null) }
            
            try {
                val result = portfolioUseCases.executeTrade(
                    cryptoId = cryptoId,
                    symbol = symbol,
                    name = name,
                    amount = amount,
                    price = price,
                    type = type
                )
                
                if (result.isSuccess) {
                    _uiState.update { it.copy(
                        isLoading = false,
                        operationSuccess = true,
                        operationMessage = if (type == TransactionType.BUY) 
                            "Successfully purchased $amount $symbol" 
                        else 
                            "Successfully sold $amount $symbol"
                    ) }
                    // Refresh portfolio data after successful trade
                    loadPortfolioData()
                } else {
                    _uiState.update { it.copy(
                        isLoading = false,
                        operationSuccess = false,
                        operationMessage = result.exceptionOrNull()?.message
                    ) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    operationSuccess = false,
                    operationMessage = e.message
                ) }
            }
        }
    }
    
    // Reset operation status after handling it in the UI
    fun resetOperationStatus() {
        _uiState.update { it.copy(operationSuccess = null, operationMessage = null) }
    }
} 