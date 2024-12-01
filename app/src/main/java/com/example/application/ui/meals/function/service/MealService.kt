package com.example.application.ui.meals.function.service

import com.example.application.Config
import com.example.application.ui.meals.function.data.MealAddRequest
import com.example.application.ui.meals.function.data.MealResponse
import com.example.application.ui.meals.function.data.MealSummaryResponse
import com.example.application.ui.meals.function.data.MealUpdateRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface MealService {
    @GET(Config.MealSummary_ENDPOINT)
    suspend fun getMealSummary(@Path("date") date : String) : MealSummaryResponse

    @GET(Config.MealDetail_ENDPOINT)
    suspend fun getMealList(@Path("date") date: String): List<MealResponse>

    @PATCH(Config.MealUpdate_ENDPOINT)
    suspend fun updateMeal(
        @Path("id") id: Int,
        @Body body: MealUpdateRequest
    )

    @DELETE(Config.MealDelete_ENDPOINT)
    suspend fun deleteMeal(
        @Path("id") id:Int
    )

    @POST(Config.MealAdd_ENDPOINT)
    suspend fun addMeal(
        @Body mealAddData : MealAddRequest
    )

}