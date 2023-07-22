package com.example.listbuyapp.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addHistory(history: History)

    @Query("SELECT * FROM history_table ORDER BY id ASC")
    fun readAllData(): LiveData<List<History>>

}
