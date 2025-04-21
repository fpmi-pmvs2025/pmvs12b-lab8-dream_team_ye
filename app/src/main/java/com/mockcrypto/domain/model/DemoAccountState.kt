package com.mockcrypto.domain.model

import java.math.BigDecimal

data class DemoAccountState(
    val balance: BigDecimal, // Например, в USD
    val totalPortfolioValue: BigDecimal, // Суммарная стоимость всех активов в портфеле
    val totalProfitLoss: BigDecimal // Общая прибыль/убыток по портфелю
)