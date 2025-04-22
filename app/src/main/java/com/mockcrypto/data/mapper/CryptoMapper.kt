package com.mockcrypto.data.mapper

import com.mockcrypto.data.model.CryptoMarketData
import com.mockcrypto.domain.model.CryptoCurrency
import java.math.BigDecimal

object CryptoMapper {

    /**
     * Maps CryptoMarketData from the API to domain CryptoCurrency model
     */
    fun mapToDomain(marketData: CryptoMarketData): CryptoCurrency {
        return CryptoCurrency(
            id = marketData.id,
            symbol = marketData.symbol.uppercase(),
            name = marketData.name,
            price = marketData.currentPrice ?: BigDecimal.ZERO,
            changePercent24h = marketData.priceChangePercentage24h ?: 0.0,
            volume24h = marketData.totalVolume ?: BigDecimal.ZERO,
            iconUrl = marketData.image,
            sparklineData = marketData.sparklineIn7d?.price
        )
    }

    /**
     * Maps a list of CryptoMarketData to a list of domain CryptoCurrency models
     */
    fun mapToDomain(marketDataList: List<CryptoMarketData>): List<CryptoCurrency> {
        return marketDataList.map { mapToDomain(it) }
    }
} 