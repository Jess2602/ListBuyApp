package com.example.listbuyapp.data

import androidx.lifecycle.LiveData

class HistoryRepository(private val historyDao: HistoryDao) {

    val readAllData: LiveData<List<History>> = historyDao.readAllData()

    suspend fun addHistory(history: History) {
        historyDao.addHistory(history)
    }
}