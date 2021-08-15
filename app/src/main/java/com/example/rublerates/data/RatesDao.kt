package com.example.rublerates.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface RatesDao {
    @Insert
    fun insertRates(rates: Rates): Completable

    @Query("SELECT * FROM rates")
    fun getAllRates(): Flowable<List<Rates>>

    @Query("DELETE FROM rates")
    fun deleteAll(): Completable


}