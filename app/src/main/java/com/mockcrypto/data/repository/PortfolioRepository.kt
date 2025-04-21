package com.mockcrypto.data.repository

import com.mockcrypto.domain.model.DemoAccountState
import com.mockcrypto.domain.model.PortfolioItem
import com.mockcrypto.domain.model.Transaction
import com.mockcrypto.domain.model.TransactionType
import kotlinx.coroutines.delay
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

interface PortfolioRepository {
    suspend fun getDemoAccountState(): Result<DemoAccountState>
    suspend fun getPortfolioItems(): Result<List<PortfolioItem>>
    suspend fun getTransactionHistory(): Result<List<Transaction>>
    suspend fun simulateTrade(
        cryptoId: String,
        symbol: String, // Добавим символ для удобства
        name: String, // Добавим имя для удобства
        amount: BigDecimal,
        price: BigDecimal,
        type: TransactionType
    ): Result<Unit> // Просто сообщаем об успехе/неудаче
    
    // New method to reset portfolio data
    suspend fun resetPortfolio(): Result<Unit>
}

// --- МОК РЕАЛИЗАЦИЯ ---
class MockPortfolioRepository : PortfolioRepository {

    // Initial state values
    private val INITIAL_BALANCE = BigDecimal("10000.00")
    private val INITIAL_PORTFOLIO = listOf(
        PortfolioItem("bitcoin", "BTC", "Bitcoin", BigDecimal("0.05"), BigDecimal("60000.00"), BigDecimal("68500.50")),
        PortfolioItem("ethereum", "ETH", "Ethereum", BigDecimal("1.5"), BigDecimal("3200.00"), BigDecimal("3500.75"))
    )
    private val INITIAL_TRANSACTIONS = listOf(
        Transaction(UUID.randomUUID().toString(), "bitcoin", "BTC", TransactionType.BUY, BigDecimal("0.05"), BigDecimal("60000.00"), LocalDateTime.now().minusDays(5)),
        Transaction(UUID.randomUUID().toString(), "ethereum", "ETH", TransactionType.BUY, BigDecimal("1.5"), BigDecimal("3200.00"), LocalDateTime.now().minusDays(3))
    )

    // Mutable state
    private var currentBalance = INITIAL_BALANCE
    private val portfolio = INITIAL_PORTFOLIO.toMutableList()
    private val transactions = INITIAL_TRANSACTIONS.toMutableList()

    override suspend fun getDemoAccountState(): Result<DemoAccountState> {
        delay(300)
        val totalValue = portfolio.sumOf { it.currentValue }
        val totalCostBasis = portfolio.sumOf { it.amount * it.averageBuyPrice } // Упрощенный расчет P/L
        val totalProfitLoss = totalValue - totalCostBasis // Очень упрощенно!
        return Result.success(DemoAccountState(currentBalance, totalValue, totalProfitLoss))
    }

    override suspend fun getPortfolioItems(): Result<List<PortfolioItem>> {
        delay(400)
        // В реальном приложении нужно обновить currentPrice из CryptoRepository
        return Result.success(portfolio.toList())
    }

    override suspend fun getTransactionHistory(): Result<List<Transaction>> {
        delay(200)
        return Result.success(transactions.sortedByDescending { it.timestamp })
    }

    override suspend fun simulateTrade(
        cryptoId: String,
        symbol: String,
        name: String,
        amount: BigDecimal,
        price: BigDecimal,
        type: TransactionType
    ): Result<Unit> {
        delay(500) // Имитация обработки сделки
        val cost = amount * price
        val transactionId = UUID.randomUUID().toString()

        return try {
            when (type) {
                TransactionType.BUY -> {
                    if (currentBalance >= cost) {
                        currentBalance -= cost
                        // Добавить или обновить позицию в портфеле (логика усреднения не реализована!)
                        val existingItem = portfolio.find { it.cryptoId == cryptoId }
                        if (existingItem != null) {
                            // TODO: Реализовать логику усреднения цены покупки
                            val newAmount = existingItem.amount + amount
                            portfolio.remove(existingItem)
                            portfolio.add(existingItem.copy(amount = newAmount, currentPrice = price /* Обновляем для согласованности */))
                        } else {
                            portfolio.add(PortfolioItem(cryptoId, symbol, name, amount, price, price))
                        }
                        transactions.add(Transaction(transactionId, cryptoId, symbol, type, amount, price, LocalDateTime.now()))
                        Result.success(Unit)
                    } else {
                        Result.failure(Exception("Insufficient balance"))
                    }
                }
                TransactionType.SELL -> {
                    val existingItem = portfolio.find { it.cryptoId == cryptoId }
                    if (existingItem != null && existingItem.amount >= amount) {
                        currentBalance += cost
                        val newAmount = existingItem.amount - amount
                        portfolio.remove(existingItem)
                        if (newAmount > BigDecimal.ZERO) {
                            portfolio.add(existingItem.copy(amount = newAmount, currentPrice = price))
                        }
                        transactions.add(Transaction(transactionId, cryptoId, symbol, type, amount, price, LocalDateTime.now()))
                        Result.success(Unit)
                    } else {
                        Result.failure(Exception("Not enough ${symbol} to sell or item not found"))
                    }
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun resetPortfolio(): Result<Unit> {
        delay(1000) // Simulate processing time
        
        try {
            // Reset to initial state
            currentBalance = INITIAL_BALANCE
            portfolio.clear()
            portfolio.addAll(INITIAL_PORTFOLIO)
            transactions.clear()
            transactions.addAll(INITIAL_TRANSACTIONS)
            
            return Result.success(Unit)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
}