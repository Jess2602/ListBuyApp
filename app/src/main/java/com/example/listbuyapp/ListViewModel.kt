package com.example.listbuyapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ListViewModel : ViewModel() {
    var listName = MutableLiveData<String>()
    var listCategory = MutableLiveData<String>()

}