package com.example.rublerates.data

import androidx.room.Dao
import androidx.room.Query
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface BankDao {
    @Query("SELECT * FROM banks WHERE bank_name = :bank_name")
    fun getBankByName(bank_name: String) : Single<Bank>

    @Query("SELECT * FROM banks WHERE id = :bank_id")
    fun getBankById(bank_id: Int) : Single<Bank>

    @Query("SELECT * FROM banks")
    fun getAllBanks(): Flowable<List<Bank>>
}