package com.example.rublerates.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Bank::class, Rates::class], version = 1)
abstract class RatesDatabase : RoomDatabase() {
    abstract fun ratesDao(): RatesDao
    abstract fun bankDao(): BankDao

    companion object {
        @Volatile
        private var INSTANCE: RatesDatabase? = null
        private const val INSERT_BANKS = "INSERT INTO banks (bank_name, russian_name) VALUES ('Sberbank', 'Сбербанк'), ('Alfabank', 'Альфабанк'), ('Otkritie', 'Открытие'), ('Raiffeisen', 'Райффайзен'), ('VTB', 'ВТБ'), ('Rosselkhozbank', 'Россельхозбанк'), ('MKB', 'МКБ'), ('Gazprombank', 'Газпромбанк')"

        fun getDatabase(context: Context): RatesDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RatesDatabase::class.java,
                    "rates_database"
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            db.execSQL(INSERT_BANKS)
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}