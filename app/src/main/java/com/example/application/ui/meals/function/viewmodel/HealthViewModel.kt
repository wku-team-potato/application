package com.example.application.ui.meals.function.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.application.ui.meals.function.data.HealthResponse
import com.example.application.ui.meals.function.repository.HealthRepository

class HealthViewModel(private val repository: HealthRepository) : ViewModel() {
    private val _weightList = MutableLiveData<List<HealthResponse>>()
    val weightList: LiveData<List<HealthResponse>> get() = _weightList

    suspend fun getWeightList() {
        try {
            val response = repository.getWeightList()
            _weightList.postValue(response)
        } catch (e: Exception) {
            _weightList.postValue(emptyList())
        }
    }
}