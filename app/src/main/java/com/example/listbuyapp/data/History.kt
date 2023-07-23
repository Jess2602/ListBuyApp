package com.example.listbuyapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history_table")

class History(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var listName_history: String,
    var listCategory_history: String,
    var listDate_history: String,
)
