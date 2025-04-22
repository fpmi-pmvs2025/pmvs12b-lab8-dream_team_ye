package com.mockcrypto.data.repository

import com.mockcrypto.domain.model.CryptoCurrency
import kotlinx.coroutines.delay
import java.math.BigDecimal
import kotlin.random.Random

interface CryptoRepository {
    suspend fun getCryptoList(): Result<List<CryptoCurrency>>
    suspend fun getCryptoDetails(id: String): Result<CryptoCurrency>
}

// --- МОК РЕАЛИЗАЦИЯ (заменится реальной позже) ---
class MockCryptoRepository : CryptoRepository {

    private val mockCryptos = listOf(
        CryptoCurrency("bitcoin", "BTC", "Bitcoin", BigDecimal("68500.50"), 2.5, BigDecimal("35000000000"), "https://coin-images.coingecko.com/coins/images/1/large/bitcoin.png?1696501400"),
        CryptoCurrency("ethereum", "ETH", "Ethereum", BigDecimal("3500.75"), -1.2, BigDecimal("18000000000"), "https://coin-images.coingecko.com/coins/images/279/large/ethereum.png?1696501628"),
        CryptoCurrency("solana", "SOL", "Solana", BigDecimal("150.20"), 5.8, BigDecimal("5000000000"), "https://coin-images.coingecko.com/coins/images/4128/large/solana.png?1718769756"),
        CryptoCurrency("dogecoin", "DOGE", "Dogecoin", BigDecimal("0.158"), 0.5, BigDecimal("1500000000"), "https://coin-images.coingecko.com/coins/images/5/large/dogecoin.png?1696501409"),
        CryptoCurrency("cardano", "ADA", "Cardano", BigDecimal("0.65"), -0.8, BigDecimal("800000000"), "https://coin-images.coingecko.com/coins/images/975/large/cardano.png?1696502090")
    )

    override suspend fun getCryptoList(): Result<List<CryptoCurrency>> {
        delay(3000) // Имитация загрузки
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