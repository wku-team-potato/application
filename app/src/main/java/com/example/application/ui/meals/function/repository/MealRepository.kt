package com.example.application.ui.meals.function.repository

import android.util.Log
import com.example.application.ui.meals.function.data.MealAddRequest
import com.example.application.ui.meals.function.data.MealResponse
import com.example.application.ui.meals.function.data.MealSummaryResponse
import com.example.application.ui.meals.function.data.MealUpdateRequest
import com.example.application.ui.meals.function.service.MealService

class MealRepository(private val service: MealService) {
    suspend fun getMealSummary(date: String): MealSummaryResponse {
        val response = service.getMealSummary(date)
        Log.d("MealRepository", "Requesting meal summary for date: $date")
        Log.d("MealRepository", "API response: $response")
        return response
    }

    suspend fun getMealList(date: String): List<MealResponse> {
        val response = service.getMealList(date)
        Log.d("MealRepository", "Meal List API Response: $response")
        return response
    }

    suspend fun updateMeal(id: Int, request: MealUpdateRequest) {
        try {
            service.updateMeal(id, request)
            Log.d("MealRepository", "Meal updated successfully: $id")
        } catch (e: Exception) {
            Log.e("MealRepository", "Error updating meal with id $id: ${e.message}")
            throw e
        }
    }

    suspend fun deleteMeal(id: Int) {
        try {
            service.deleteMeal(id)
            Log.d("MealRepository", "Meal deleted successfully: $id")
        } catch (e: Exception) {
            Log.e("MealRepository", "Error deleting meal with id $id: ${e.message}")
            throw e
        }
    }

    suspend fun addMeal(foodId: Int, servingSize: Int, mealType: String, date: String) {
        val mealData = MealAddRequest(
            food_id = foodId,
            serving_size = servingSize,
            meal_type = mealType,
            date = date
        )
        service.addMeal(mealData)
    }
}