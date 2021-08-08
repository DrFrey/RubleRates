package com.example.rublerates.data


import com.google.gson.annotations.SerializedName

data class VtbRates(
    @SerializedName("DateFrom")
    val dateFrom: String?,
    @SerializedName("GroupedRates")
    val groupedRates: List<GroupedRate?>?,
    @SerializedName("IsRightPlaceholder")
    val isRightPlaceholder: Boolean?,
    @SerializedName("MoneyCurrencies")
    val moneyCurrencies: List<MoneyCurrency?>?
) {
    data class GroupedRate(
        @SerializedName("MoneyRates")
        val moneyRates: List<MoneyRate?>?
    ) {
        data class MoneyRate(
            @SerializedName("BankBuyAt")
            val bankBuyAt: Double?,
            @SerializedName("BankSellAt")
            val bankSellAt: Double?,
            @SerializedName("CbRate")
            val cbRate: Double?,
            @SerializedName("FromCurrency")
            val fromCurrency: FromCurrency?,
            @SerializedName("FromCurrencyCount")
            val fromCurrencyCount: Int?,
            @SerializedName("IsBankBuyAtRaised")
            val isBankBuyAtRaised: Boolean?,
            @SerializedName("IsBankSellAtRaised")
            val isBankSellAtRaised: Boolean?,
            @SerializedName("MinAmount")
            val minAmount: Int?,
            @SerializedName("NodeDescription")
            val nodeDescription: String?,
            @SerializedName("StartDate")
            val startDate: String?,
            @SerializedName("ToCurrency")
            val toCurrency: ToCurrency?
        ) {
            data class FromCurrency(
                @SerializedName("Code")
                val code: String?,
                @SerializedName("Name")
                val name: String?,
                @SerializedName("Symbol")
                val symbol: String?
            )

            data class ToCurrency(
                @SerializedName("Code")
                val code: String?,
                @SerializedName("Name")
                val name: String?,
                @SerializedName("Symbol")
                val symbol: String?
            )
        }
    }

    data class MoneyCurrency(
        @SerializedName("Code")
        val code: String?,
        @SerializedName("IsPublished")
        val isPublished: Boolean?,
        @SerializedName("ItemName")
        val itemName: String?,
        @SerializedName("Name")
        val name: String?,
        @SerializedName("Symbol")
        val symbol: String?,
        @SerializedName("ValueTypeId")
        val valueTypeId: String?
    )
}