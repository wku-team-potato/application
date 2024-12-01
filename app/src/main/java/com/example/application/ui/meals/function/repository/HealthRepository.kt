package com.example.application.ui.meals.function.repository

import com.example.application.ui.meals.function.data.HealthResponse
import com.example.application.ui.meals.function.service.HealthService

class HealthRepository(private val api: HealthService) {
    suspend fun getWeightList(): List<HealthResponse> {
        return api.getHeightWeightList()
    }
}