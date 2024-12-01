package com.example.application

import com.example.application.ui.auth.functions.service.FirstPersonalInfoService
import com.example.application.ui.auth.functions.service.PersonalInfoService
import com.example.application.ui.auth.functions.service.SignInService
import com.example.application.ui.auth.functions.service.SignUpService
import com.example.application.ui.leaderboard.function.service.LeaderBoardService
import com.example.application.ui.meals.function.service.FoodService
import com.example.application.ui.meals.function.service.HealthService
import com.example.application.ui.meals.function.service.MealService
import com.example.application.ui.profile.function.service.ProfileService
import com.example.application.ui.reward.function.service.RewardService
import com.example.application.ui.store.functions.service.ProfilePointService
import com.example.application.ui.store.functions.service.StoreService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val sessionManager: SessionManager = MyApplication.getSessionManager()

    private val client = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(sessionManager))  // AuthInterceptor가 자동으로 토큰, 세션id를 추가
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Config.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val signUpService: SignUpService by lazy {
        retrofit.create(SignUpService::class.java)
    }

    val signInService: SignInService by lazy {
        retrofit.create(SignInService::class.java)
    }

    val firstPersonalInfoService : FirstPersonalInfoService by lazy{
        retrofit.create(FirstPersonalInfoService::class.java)
    }

    val personalInfoService : PersonalInfoService by lazy{
        retrofit.create(PersonalInfoService::class.java)
    }

    val storeService : StoreService by lazy{
        retrofit.create(StoreService::class.java)
    }

    val profilePointService : ProfilePointService by lazy{
        retrofit.create(ProfilePointService::class.java)
    }

    val profileService : ProfileService by lazy{
        retrofit.create(ProfileService::class.java)
    }

    val rewardService : RewardService by lazy{
        retrofit.create(RewardService::class.java)
    }

    val leaderBoardService : LeaderBoardService by lazy{
        retrofit.create(LeaderBoardService::class.java)
    }

    val mealService : MealService by lazy{
        retrofit.create(MealService::class.java)
    }

    val healthService : HealthService by lazy{
        retrofit.create(HealthService::class.java)
    }

    val foodService : FoodService by lazy{
        retrofit.create(FoodService::class.java)
    }
}