package com.example.application.ui.meals

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.application.R
import com.example.application.RetrofitInstance
import com.example.application.databinding.ActivityCalorieBinding
import com.example.application.databinding.ItemCalorieBinding
import com.example.application.ui.meals.function.repository.MealRepository
import com.example.application.ui.meals.function.viewmodel.MealViewModel
import com.example.application.ui.meals.function.viewmodel.MealViewModelFactory

class CalorieActivity : AppCompatActivity() {
    private val binding by lazy { ActivityCalorieBinding.inflate(layoutInflater) }
    private lateinit var mealViewModel: MealViewModel
    private lateinit var selectedDate : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        val repository = MealRepository(RetrofitInstance.mealService)
        val factory = MealViewModelFactory(repository)
        mealViewModel = ViewModelProvider(this, factory).get(MealViewModel::class.java)

        selectedDate = intent.getStringExtra("selectedDate") ?: ""

        Log.d("CalorieActivity", "Received date for API : $selectedDate")

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initUi()
        fetchMealSummary()
    }

    override fun onResume() {
        super.onResume()
        Log.d("CalorieActivity", "onResume called, refreshing data for date: $selectedDate")
        fetchMealSummary()
    }

    private fun initUi() = with(binding) {
        toolbar.setNavigationOnClickListener { finish() }
        recyclerView.layoutManager = LinearLayoutManager(this@CalorieActivity)
    }

    private fun fetchMealSummary() {
        Log.d("CalorieActivity", "Fetching data for date: $selectedDate")

        mealViewModel.mealSummary.observe(this) { mealSummary ->
            val mealList = listOf("아침", "점심", "저녁")
            val calorieData = listOf(
                mealSummary.breakfast.calorie.toIntOrZero(),
                mealSummary.lunch.calorie.toIntOrZero(),
                mealSummary.dinner.calorie.toIntOrZero()
            )
            updateAdapter(mealList, calorieData)
        }

        // ViewModel에 API 요청 전달
        mealViewModel.loadMealSummary(selectedDate)
    }

    private fun updateAdapter(mealList: List<String>, calorieData: List<Int>) {
        binding.recyclerView.adapter = CalorieAdapter(mealList, calorieData).apply {
            onItemClickListener = { mealTime ->
                Log.d("CalorieActivity", "Clicked on: $mealTime")

                val mealType = when (mealTime){
                    "아침" -> "breakfast"
                    "점심" -> "lunch"
                    "저녁" -> "dinner"
                    else -> ""
                }

                val intent = Intent(this@CalorieActivity, MealActivity::class.java).apply {
                    putExtra("title", mealTime)
                    putExtra("date", selectedDate)
                    putExtra("mealType", mealType)
                }
                startActivity(intent)
            }
        }
    }

    // 확장 함수: Double -> Int 변환, null 또는 빈 값 처리
    private fun Double?.toIntOrZero(): Int = this?.toInt() ?: 0
}