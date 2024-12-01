package com.example.application.ui.meals.function.data

data class MealSummaryResponse(
    val breakfast: MealDetailResponse = MealDetailResponse(),
    val lunch: MealDetailResponse = MealDetailResponse(),
    val dinner: MealDetailResponse = MealDetailResponse(),
    val summary: MealTotalResponse = MealTotalResponse()
)

data class MealDetailResponse(
    val calorie: Double = 0.0,
    val carbohydrate: Double = 0.0,
    val protein: Double = 0.0,
    val fat: Double = 0.0
)

data class MealTotalResponse(
    val calorie: Double = 0.0,
    val carbohydrate: Double = 0.0,
    val protein: Double = 0.0,
    val fat: Double = 0.0
)

data class FoodDetail(
    val food_name: String,
    val serving_size: Int
)

data class MealResponse(
    val id: Int,
    val food_id : Int,
    val meal_type: String,
    val food: FoodDetail,
    var serving_size: Int,
    var count: Int = 1
)