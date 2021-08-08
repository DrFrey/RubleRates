package com.example.rublerates.data


import com.google.gson.annotations.SerializedName

data class OpenRates(
    @SerializedName("cityId")
    val cityId: Int?,
    @SerializedName("currencyRates")
    val currencyRates: CurrencyRates?,
    @SerializedName("defaults")
    val defaults: Defaults?,
    @SerializedName("params")
    val params: Params?
) {
    data class CurrencyRates(
        @SerializedName("cards")
        val cards: Cards?,
        @SerializedName("cash")
        val cash: Cash?,
        @SerializedName("nonCash")
        val nonCash: NonCash?,
        @SerializedName("online")
        val online: Online?
    ) {
        data class Cards(
            @SerializedName("title")
            val title: String?,
            @SerializedName("value")
            val value: List<Any?>?
        )

        data class Cash(
            @SerializedName("title")
            val title: String?,
            @SerializedName("value")
            val value: List<Value?>?
        ) {
            data class Value(
                @SerializedName("createdAt")
                val createdAt: String?,
                @SerializedName("from")
                val from: String?,
                @SerializedName("priceCurrency")
                val priceCurrency: String?,
                @SerializedName("purchasePrice")
                val purchasePrice: Double?,
                @SerializedName("range")
                val range: Int?,
                @SerializedName("salePrice")
                val salePrice: Double?,
                @SerializedName("to")
                val to: String?
            )
        }

        data class NonCash(
            @SerializedName("title")
            val title: String?,
            @SerializedName("value")
            val value: List<Value?>?
        ) {
            data class Value(
                @SerializedName("createdAt")
                val createdAt: String?,
                @SerializedName("from")
                val from: String?,
                @SerializedName("priceCurrency")
                val priceCurrency: String?,
                @SerializedName("purchasePrice")
                val purchasePrice: Double?,
                @SerializedName("range")
                val range: Int?,
                @SerializedName("salePrice")
                val salePrice: Double?,
                @SerializedName("to")
                val to: String?
            )
        }

        data class Online(
            @SerializedName("title")
            val title: String?,
            @SerializedName("value")
            val value: List<Value?>?
        ) {
            data class Value(
                @SerializedName("createdAt")
                val createdAt: String?,
                @SerializedName("from")
                val from: String?,
                @SerializedName("priceCurrency")
                val priceCurrency: String?,
                @SerializedName("purchasePrice")
                val purchasePrice: Double?,
                @SerializedName("range")
                val range: Int?,
                @SerializedName("salePrice")
                val salePrice: Double?,
                @SerializedName("to")
                val to: String?
            )
        }
    }

    data class Defaults(
        @SerializedName("amount")
        val amount: Int?,
        @SerializedName("purchasingCurrency")
        val purchasingCurrency: String?,
        @SerializedName("sellingCurrency")
        val sellingCurrency: String?
    )

    data class Params(
        @SerializedName("benefits")
        val benefits: Benefits?,
        @SerializedName("exchangeRatesFor")
        val exchangeRatesFor: List<ExchangeRatesFor?>?,
        @SerializedName("links")
        val links: Links?
    ) {
        data class Benefits(
            @SerializedName("cards")
            val cards: String?,
            @SerializedName("cash")
            val cash: String?,
            @SerializedName("nonCash")
            val nonCash: String?,
            @SerializedName("nonCashYr")
            val nonCashYr: String?,
            @SerializedName("online")
            val online: String?
        )

        data class ExchangeRatesFor(
            @SerializedName("text")
            val text: String?,
            @SerializedName("url")
            val url: String?
        )

        data class Links(
            @SerializedName("store")
            val store: Store?,
            @SerializedName("tab")
            val tab: List<Tab?>?
        ) {
            data class Store(
                @SerializedName("apple")
                val apple: String?,
                @SerializedName("google")
                val google: String?
            )

            data class Tab(
                @SerializedName("text")
                val text: String?,
                @SerializedName("url")
                val url: String?
            )
        }
    }
}