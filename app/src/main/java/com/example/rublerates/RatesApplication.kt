package com.example.rublerates

import android.app.Application
import com.example.rublerates.data.RatesDatabase
import com.example.rublerates.data.RatesRepository

class RatesApplication : Application() {
    companion object {
        lateinit var instance: RatesApplication

        private val database by lazy {
            RatesDatabase.getDatabase(instance)
        }
        val repository by lazy {
            RatesRepository(database.ratesDao(), database.bankDao())
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}