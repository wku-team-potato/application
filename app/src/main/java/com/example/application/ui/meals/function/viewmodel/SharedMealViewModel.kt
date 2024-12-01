package com.example.application.ui.meals.function.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedMealViewModel : ViewModel() {
    private val _refreshTrigger = MutableLiveData<Boolean>()
    val refreshTrigger: LiveData<Boolean> get() = _refreshTrigger

    fun triggerRefresh() {
        Log.d("SharedMealViewModel", "Refresh triggered")
        _refreshTrigger.value = true
    }
}