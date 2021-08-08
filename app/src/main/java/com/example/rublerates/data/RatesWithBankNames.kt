package com.example.rublerates.data

import androidx.room.ColumnInfo

data class RatesWithBankNames(
    val bankName: String,
    val rusName: String,
    val date: Long,
    val bankUsdBuy: Double,
    val bankUsdSell: Double,
    val bankEurBuy: Double,
    val bankEurSell: Double
)