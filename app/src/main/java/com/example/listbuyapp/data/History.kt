package com.example.listbuyapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history_table")

class History(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val listName_history: String,
    val listCategory_history: String,
    val listDate_history: String,
)
