package com.mockcrypto.data.repository

import com.mockcrypto.domain.model.CryptoCurrency
import kotlinx.coroutines.delay
import java.math.BigDecimal
import kotlin.random.Random

interface CryptoRepository {
    suspend fun getCryptoList(): Result<List<CryptoCurrency>> // Используем Result для обработки ошибок
    suspend fun getCryptoDetails(id: String): Result<CryptoCurrency>
}

// --- МОК РЕАЛИЗАЦИЯ (заменится реальной позже) ---
class MockCryptoRepository : CryptoRepository {

    private val mockCryptos = listOf(
        CryptoCurrency("bitcoin", "BTC", "Bitcoin", BigDecimal("68500.50"), 2.5, BigDecimal("35000000000"), "https://example.com/btc.png"),
        CryptoCurrency("ethereum", "ETH", "Ethereum", BigDecimal("3500.75"), -1.2, BigDecimal("18000000000"), "https://example.com/eth.png"),
        CryptoCurrency("solana", "SOL", "Solana", BigDecimal("150.20"), 5.8, BigDecimal("5000000000"), "https://example.com/sol.png"),
        CryptoCurrency("dogecoin", "DOGE", "Dogecoin", BigDecimal("0.158"), 0.5, BigDecimal("1500000000"), "https://example.com/doge.png"),
        CryptoCurrency("cardano", "ADA", "Cardano", BigDecimal("0.65"), -0.8, BigDecimal("800000000"), "https://example.com/ada.png")
    )

    override suspend fun getCryptoList(): Result<List<CryptoCurrency>> {
        delay(1000) // Имитация загрузки
        // Имитация возможной ошибки
        // if (Random.nextBoolean()) return Result.failure(Exception("Network Error"))
        return Result.success(mockCryptos.shuffled()) // Возвращаем перемешанный список
    }

    override suspend fun getCryptoDetails(id: String): Result<CryptoCurrency> {
        delay(500) // Имитация загрузки
        val crypto = mockCryptos.find { it.id == id }
        return if (crypto != null) {
            // Добавим немного случайности в детали для наглядности
            Result.success(
                crypto.copy(
                    price = crypto.price * BigDecimal(Random.nextDouble(0.99, 1.01)),
                    changePercent24h = Random.nextDouble(-5.0, 5.0)
                )
            )
        } else {
            Result.failure(NoSuchElementException("Crypto with id $id not found"))
        }
    }
}