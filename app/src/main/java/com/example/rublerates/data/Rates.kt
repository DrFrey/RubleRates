package com.example.rublerates.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rates")
data class Rates(

    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo(name = "bank_id")
    val bankId: Int,

    @ColumnInfo(name = "date")
    val date: Long,

    @ColumnInfo(name = "bank_usd_buy")
    val bankUsdBuy: Double,

    @ColumnInfo(name = "bank_usd_sell")
    val bankUsdSell: Double,

    @ColumnInfo(name = "bank_eur_buy")
    val bankEurBuy: Double,

    @ColumnInfo(name = "bank_eur_sell")
    val bankEurSell: Double

)
