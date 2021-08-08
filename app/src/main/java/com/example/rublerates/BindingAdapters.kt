package com.example.rublerates

import androidx.databinding.BindingAdapter
import com.google.android.material.textview.MaterialTextView

@BindingAdapter("convertDouble")
fun convertDouble(textView: MaterialTextView, double: Double) {
    textView.text = double.toString()
}