package com.example.application.ui.meals.function.service

import com.example.application.Config
import com.example.application.ui.meals.function.data.FoodResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface FoodService {

    @GET(Config.MealSearch_ENDPOINT)
    suspend fun searchFood(@Path("food_name") foodName : String) : List<FoodResponse>
}