package com.example.application

object Config {
    const val BASE_URL = "http://4.217.250.21:8000/api/v1/"

    const val SIGNIN_ENDPOINT = "auth/login/"
    const val SIGNUP_ENDPOINT = "auth/signup/"
    const val PersonalInfo_ENDPOINT = "profile/heightweightrecord/list/"
    const val PersonalInfoCreate_ENDPOINT = "profile/heightweight/update/"
    const val Store_ENDPOINT = "store/items/list/"
    const val Point_ENDPOINT = "profile/totalpoints/retrieve/"
    const val PointBuy_ENDPOINT = "store/items/{id}/buy/"
    const val Profile_ENDPOINT = "profile/heightweightrecord/list/"
    const val ProfileUpdate_ENDPOINT = "profile/usernameheightweight/update/"
    const val REWARD_ENDPOINT = "point/transaction/"
    const val MyLeaderBoard_ENDPOINT = "profile/rankings/my/"
    const val TopLeaderBoard_ENDPOINT = "profile/rankings/top3/"
    const val MealSummary_ENDPOINT = "meal/summary/{date}/"
    const val MealDetail_ENDPOINT = "meal/list/{date}"
    const val MealUpdate_ENDPOINT = "meal/update/{id}"
    const val MealDelete_ENDPOINT = "meal/delete/{id}"
    const val MealSearch_ENDPOINT = "nutrition/foods/search/name/{food_name}/"
    const val MealAdd_ENDPOINT = "meal/create/"
}