package com.mockcrypto.domain.model

import java.math.BigDecimal

data class CryptoCurrency(
    val id: String,
    val symbol: String, // Например, "BTC"
    val name: String,   // Например, "Bitcoin"
    val price: BigDecimal,
    val changePercent24h: Double,
    val volume24h: BigDecimal,
    val iconUrl: String? = null // Опциональная ссылка на иконку
)