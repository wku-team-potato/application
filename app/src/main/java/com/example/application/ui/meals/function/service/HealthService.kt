package com.example.application.ui.meals.function.service

import com.example.application.Config
import com.example.application.ui.meals.function.data.HealthResponse
import retrofit2.http.GET

interface HealthService {
    @GET(Config.Profile_ENDPOINT)
    suspend fun getHeightWeightList(): List<HealthResponse>
}