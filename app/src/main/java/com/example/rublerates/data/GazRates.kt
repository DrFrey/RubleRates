package com.example.rublerates.data


import com.google.gson.annotations.SerializedName

class GazRates : ArrayList<GazRates.GazRatesItem>(){
    data class GazRatesItem(
        @SerializedName("content")
        val content: List<Content?>?,
        @SerializedName("converter")
        val converter: List<Converter?>?,
        @SerializedName("converterCurrencies")
        val converterCurrencies: List<ConverterCurrency?>?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("notificator")
        val notificator: String?,
        @SerializedName("showCity")
        val showCity: Boolean?
    ) {
        data class Content(
            @SerializedName("id")
            val id: Int?,
            @SerializedName("items")
            val items: List<Item?>?,
            @SerializedName("name")
            val name: String?,
            @SerializedName("updated")
            val updated: String?
        ) {
            data class Item(
                @SerializedName("buy")
                val buy: String?,
                @SerializedName("buyDirection")
                val buyDirection: String?,
                @SerializedName("currency")
                val currency: String?,
                @SerializedName("currencyTitle")
                val currencyTitle: String?,
                @SerializedName("isCrossCourse")
                val isCrossCourse: Boolean?,
                @SerializedName("name")
                val name: String?,
                @SerializedName("sell")
                val sell: String?,
                @SerializedName("sellDirection")
                val sellDirection: String?,
                @SerializedName("ticker")
                val ticker: String?,
                @SerializedName("tickerTitle")
                val tickerTitle: String?,
                @SerializedName("unit")
                val unit: Int?
            )
        }
    
        data class Converter(
            @SerializedName("date")
            val date: String?,
            @SerializedName("isCross")
            val isCross: Boolean?,
            @SerializedName("rate")
            val rate: String?,
            @SerializedName("units")
            val units: Int?,
            @SerializedName("val1")
            val val1: String?,
            @SerializedName("val2")
            val val2: String?
        )
    
        data class ConverterCurrency(
            @SerializedName("code")
            val code: String?,
            @SerializedName("sign")
            val sign: String?,
            @SerializedName("title")
            val title: String?
        )
    }
}