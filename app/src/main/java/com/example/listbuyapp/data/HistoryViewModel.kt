package com.example.listbuyapp.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HistoryRepository
    val readAllData: LiveData<List<History>>

    init {
        val historyDao = HistoryDatabase.getDatabase(application).historyDao()
        repository = HistoryRepository(historyDao)
        readAllData = repository.readAllData
    }

    fun addHistory(history: History) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addHistory(history)
        }
    }

    fun deleteHistoryById(historyId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteHistoryById(historyId)
        }
    }
}