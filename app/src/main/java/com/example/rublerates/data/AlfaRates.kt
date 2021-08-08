package com.example.rublerates.data


import com.google.gson.annotations.SerializedName

data class AlfaRates(
    @SerializedName("request")
    val request: Request?,
    @SerializedName("response")
    val response: Response?
) {
    data class Request(
        @SerializedName("filter")
        val filter: Filter?,
        @SerializedName("limit")
        val limit: String?,
        @SerializedName("offset")
        val offset: String?,
        @SerializedName("order")
        val order: String?,
        @SerializedName("server")
        val server: String?,
        @SerializedName("service")
        val service: String?,
        @SerializedName("version")
        val version: String?
    ) {
        data class Filter(
            @SerializedName("segment")
            val segment: String?,
            @SerializedName("text")
            val text: String?
        )
    }

    data class Response(
        @SerializedName("data")
        val `data`: Data?,
        @SerializedName("status")
        val status: String?
    ) {
        data class Data(
            @SerializedName("chf")
            val chf: List<Chf?>?,
            @SerializedName("eur")
            val eur: List<Eur?>?,
            @SerializedName("gbp")
            val gbp: List<Gbp?>?,
            @SerializedName("usd")
            val usd: List<Usd?>?
        ) {
            data class Chf(
                @SerializedName("date")
                val date: String?,
                @SerializedName("order")
                val order: String?,
                @SerializedName("type")
                val type: String?,
                @SerializedName("value")
                val value: Double?
            )

            data class Eur(
                @SerializedName("date")
                val date: String?,
                @SerializedName("order")
                val order: String?,
                @SerializedName("type")
                val type: String?,
                @SerializedName("value")
                val value: Double?
            )

            data class Gbp(
                @SerializedName("date")
                val date: String?,
                @SerializedName("order")
                val order: String?,
                @SerializedName("type")
                val type: String?,
                @SerializedName("value")
                val value: Double?
            )

            data class Usd(
                @SerializedName("date")
                val date: String?,
                @SerializedName("order")
                val order: String?,
                @SerializedName("type")
                val type: String?,
                @SerializedName("value")
                val value: Double?
            )
        }
    }
}