package com.example.application.ui.meals

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.application.Config
import com.example.application.R
import com.example.application.RetrofitInstance
import com.example.application.databinding.ActivityFoodSearchBinding
import com.example.application.ui.meals.function.data.FoodResponse
import com.example.application.ui.meals.function.data.MealAddRequest
import com.example.application.ui.meals.function.service.FoodService
import com.example.application.ui.meals.function.service.MealService
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class FoodSearchActivity : AppCompatActivity() {
    private val binding by lazy { ActivityFoodSearchBinding.inflate(layoutInflater) }
    private val foodService: FoodService by lazy { RetrofitInstance.foodService }
    private val mealService: MealService by lazy { RetrofitInstance.mealService }

    private var selectedFood: FoodResponse? = null
    private var mealType: String = ""
    private var date: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        date = intent.getStringExtra("date") ?: ""

        initUi()
    }

    private fun initUi() = with(binding) {
        backButton.setOnClickListener { finish() }

        mealTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mealType = when (position) {
                    0 -> "breakfast"
                    1 -> "lunch"
                    2 -> "dinner"
                    else -> ""
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        recyclerView.layoutManager = LinearLayoutManager(this@FoodSearchActivity)
        val foodAdapter = FoodAdapter { food, isValid ->
            if (isValid) {
                selectedFood = food
                if (food == null) {
                    Log.d("FoodSearchActivity", "Selection cleared.")
                } else {
                    Log.d("FoodSearchActivity", "Selected food: ${food.food_name}")
                }
            } else {
                Toast.makeText(this@FoodSearchActivity, "한 개씩 추가해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
        recyclerView.adapter = foodAdapter

        searchEditText.doAfterTextChanged {
            val query = it.toString().trim()
            imageContainer.isVisible = query.isBlank()
            recordContainer.isVisible = query.isNotBlank()

            if (query.isNotEmpty()) {
                fetchFoods(query, foodAdapter)
            }
        }

        recordButton.setOnClickListener {
            if (mealType.isEmpty()) {
                Toast.makeText(this@FoodSearchActivity, "식사 시간을 선택하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (selectedFood == null) {
                Toast.makeText(this@FoodSearchActivity, "음식을 선택하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            addSelectedFood()
        }
    }

    private fun fetchFoods(query: String, adapter: FoodAdapter) {
        lifecycleScope.launch {
            try {
                val foods = foodService.searchFood(query)
                adapter.submitList(foods)
            } catch (e: Exception) {
                Toast.makeText(this@FoodSearchActivity, "음식 데이터를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addSelectedFood() {
        lifecycleScope.launch {
            try {
                val mealData = MealAddRequest(
                    food_id = selectedFood!!.food_id,
                    serving_size = selectedFood!!.serving_size,
                    meal_type = mealType,
                    date = date
                )
                mealService.addMeal(mealData)
                val formattedDate = LocalDate.parse(date).format(DateTimeFormatter.ofPattern("MM월 dd일"))
                val mealTimeText = when (mealType) {
                    "breakfast" -> "아침"
                    "lunch" -> "점심"
                    "dinner" -> "저녁"
                    else -> ""
                }
                val successMessage = "$formattedDate $mealTimeText\n${selectedFood} 추가!"
                Toast.makeText(this@FoodSearchActivity, successMessage, Toast.LENGTH_SHORT).show()
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@FoodSearchActivity, "음식 추가 실패.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}