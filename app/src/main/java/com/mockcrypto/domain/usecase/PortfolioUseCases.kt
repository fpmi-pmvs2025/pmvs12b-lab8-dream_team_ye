package com.mockcrypto.domain.usecase

import com.mockcrypto.data.repository.PortfolioRepository
import com.mockcrypto.domain.model.DemoAccountState
import com.mockcrypto.domain.model.PortfolioItem
import com.mockcrypto.domain.model.Transaction
import com.mockcrypto.domain.model.TransactionType
import java.math.BigDecimal

class GetDemoAccountStateUseCase(private val repository: PortfolioRepository) {
    suspend operator fun invoke(): Result<DemoAccountState> {
        return repository.getDemoAccountState()
    }
}

class GetPortfolioItemsUseCase(private val repository: PortfolioRepository) {
    suspend operator fun invoke(): Result<List<PortfolioItem>> {
        return repository.getPortfolioItems()
    }
}

class GetTransactionHistoryUseCase(private val repository: PortfolioRepository) {
    suspend operator fun invoke(): Result<List<Transaction>> {
        return repository.getTransactionHistory()
    }
}

class ExecuteTradeUseCase(private val repository: PortfolioRepository) {
    suspend operator fun invoke(
        cryptoId: String,
        symbol: String,
        name: String,
        amount: BigDecimal,
        price: BigDecimal,
        type: TransactionType
    ): Result<Unit> {
        return repository.simulateTrade(
            cryptoId = cryptoId,
            symbol = symbol,
            name = name,
            amount = amount,
            price = price,
            type = type
        )
    }
}

data class PortfolioUseCases(
    val getDemoAccountState: GetDemoAccountStateUseCase,
    val getPortfolioItems: GetPortfolioItemsUseCase,
    val getTransactionHistory: GetTransactionHistoryUseCase,
    val executeTrade: ExecuteTradeUseCase
) 