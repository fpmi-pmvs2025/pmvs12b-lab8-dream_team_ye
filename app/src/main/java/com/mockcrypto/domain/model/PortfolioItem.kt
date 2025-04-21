package com.mockcrypto.domain.model

import java.math.BigDecimal

data class PortfolioItem(
    val cryptoId: String,
    val symbol: String,
    val name: String,
    val amount: BigDecimal,
    val averageBuyPrice: BigDecimal,
    val currentPrice: BigDecimal // Добавим текущую цену для расчета стоимости
    // val iconUrl: String? = null // Можно добавить для отображения иконки
) {
    val currentValue: BigDecimal
        get() = amount * currentPrice // Расчетная текущая стоимость
}