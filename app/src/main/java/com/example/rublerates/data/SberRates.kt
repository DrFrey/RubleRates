package com.example.rublerates.data


import com.google.gson.annotations.SerializedName

data class SberRates(
    @SerializedName("EUR")
    val eUR: EUR?,
    @SerializedName("USD")
    val uSD: USD?
) {
    data class EUR(
        @SerializedName("lotSize")
        val lotSize: Double?,
        @SerializedName("rateList")
        val rateList: List<Rate?>?,
        @SerializedName("startDateTime")
        val startDateTime: Long?
    ) {
        data class Rate(
            @SerializedName("rangeAmountBottom")
            val rangeAmountBottom: Double?,
            @SerializedName("rangeAmountUpper")
            val rangeAmountUpper: Double?,
            @SerializedName("rateBuy")
            val rateBuy: Double?,
            @SerializedName("rateSell")
            val rateSell: Double?,
            @SerializedName("symbolBuy")
            val symbolBuy: String?,
            @SerializedName("symbolSell")
            val symbolSell: String?
        )
    }

    data class USD(
        @SerializedName("lotSize")
        val lotSize: Double?,
        @SerializedName("rateList")
        val rateList: List<Rate?>?,
        @SerializedName("startDateTime")
        val startDateTime: Long?
    ) {
        data class Rate(
            @SerializedName("rangeAmountBottom")
            val rangeAmountBottom: Double?,
            @SerializedName("rangeAmountUpper")
            val rangeAmountUpper: Double?,
            @SerializedName("rateBuy")
            val rateBuy: Double?,
            @SerializedName("rateSell")
            val rateSell: Double?,
            @SerializedName("symbolBuy")
            val symbolBuy: String?,
            @SerializedName("symbolSell")
            val symbolSell: String?
        )
    }
}