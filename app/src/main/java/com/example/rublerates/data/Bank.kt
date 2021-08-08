package com.example.rublerates.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "banks")
data class Bank(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo(name = "bank_name")
    val name: String,

    @ColumnInfo(name = "russian_name")
    val rusName: String
)
