package com.example.rublerates

import android.util.Log
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.rublerates.data.Bank
import com.google.android.material.textview.MaterialTextView
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@BindingAdapter("convertDouble")
fun convertDouble(textView: MaterialTextView, double: Double) {
    textView.text = double.toString()
}

@BindingAdapter("formattedDateTime")
fun formattedDateTime(textView: TextView, dateLong: Long) {
    try {
        val ld = Instant.ofEpochMilli(dateLong).atZone(ZoneId.systemDefault()).toLocalDateTime()
        val formattedDate = ld.format(DateTimeFormatter.ofPattern("d MMMM HH:mm"))
        textView.text = formattedDate
    } catch (e: Exception) {
        textView.text = dateLong.toString()
        Log.d("___", "error formatting date: ${e.message}")
    }
}

@BindingAdapter("setLocalisedName")
fun setLocalisedName(textView: TextView, bank: Bank?) {
    val locale = RatesApplication.instance.resources.configuration.locales[0]
    if (locale.language.equals("ru")) {
        textView.text = bank?.rusName
    } else {
        textView.text = bank?.name
    }
}