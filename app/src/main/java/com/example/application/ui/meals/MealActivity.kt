package com.example.application.ui.meals

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.application.R
import com.example.application.RetrofitInstance
import com.example.application.databinding.ActivityMealsBinding
import com.example.application.databinding.ItemFoodInMealsBinding
import com.example.application.ui.meals.function.data.MealResponse
import com.example.application.ui.meals.function.repository.MealRepository
import com.example.application.ui.meals.function.viewmodel.MealViewModel
import com.example.application.ui.meals.function.viewmodel.MealViewModelFactory
import com.example.application.ui.meals.function.viewmodel.SharedMealViewModel
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

class MealActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMealsBinding.inflate(layoutInflater) }
    private val sharedMealViewModel: SharedMealViewModel by viewModels()
    private val viewModel: MealViewModel by viewModels {
        MealViewModelFactory(MealRepository(RetrofitInstance.mealService))
    }

    private val modifiedList = mutableListOf<MealResponse>()
    private val deletedList = mutableListOf<MealResponse>()

    private var selectedDate: String = ""
    private var mealType: String = ""


    private val adapter by lazy {
        MealAdapter(
            onItemModified = { meal, count, quantity ->
                val updatedServingSize = count * quantity
                meal.serving_size = updatedServingSize
                if (!modifiedList.contains(meal)) {
                    modifiedList.add(meal)
                }
            },
            onItemRemoved = { meal ->
                if (!deletedList.contains(meal)) {
                    deletedList.add(meal)
                }
                modifiedList.remove(meal)
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        selectedDate = intent.getStringExtra("date") ?: ""
        mealType = intent.getStringExtra("mealType") ?: ""

        Log.d("MealActivity", "Selected date: $selectedDate, MealType: $mealType")

        viewModel.fetchMealList(selectedDate)
        viewModel.mealList.observe(this) { meals ->
            val filteredMeals = meals.filter { it.meal_type == mealType }
            adapter.setItems(filteredMeals)
        }

        initUi()
        observeMealData(mealType)
        fetchMealList(selectedDate)
    }


    private fun initUi() = with(binding) {
        toolbar.setNavigationOnClickListener { finish() }
        toolbar.title = intent.getStringExtra("title") ?: ""

        recyclerView.layoutManager = LinearLayoutManager(this@MealActivity)
        recyclerView.adapter = adapter

        addFoodButton.setOnClickListener {
            val intent = Intent(this@MealActivity, FoodSearchActivity::class.java)
            intent.putExtra("date", selectedDate)
            startActivity(intent)
        }

        doneButton.setOnClickListener {
            val selectedDate = intent.getStringExtra("date") ?: ""
            processChanges(selectedDate)
            Toast.makeText(this@MealActivity, "수정 사항이 저장되었습니다.", Toast.LENGTH_SHORT).show()
            sharedMealViewModel.triggerRefresh()
            finish()
        }
    }

    private fun fetchMealList(date: String) {
        viewModel.fetchMealList(date)
    }

    private fun observeMealData(mealType: String) {
        viewModel.mealList.observe(this) { meals ->
            val filteredMeals = meals.filter { it.meal_type == mealType }

            if (filteredMeals.isEmpty()) {
                binding.recyclerView.visibility = View.GONE
                binding.emptyMessage.visibility = View.VISIBLE
            } else {
                binding.recyclerView.visibility = View.VISIBLE
                binding.emptyMessage.visibility = View.GONE
                adapter.setItems(filteredMeals)
            }
        }
    }

    private fun processChanges(date: String) {
        lifecycleScope.launch {
            try {
                modifiedList.forEach { meal ->
                    Log.d("MealActivity", "Processing update for meal: $meal")
                    viewModel.updateMeal(
                        id = meal.id,
                        foodId = meal.food_id,
                        mealType = meal.meal_type,
                        servingSize = meal.serving_size,
                        date = date
                    )
                }

                deletedList.forEach { meal ->
                    Log.d("MealActivity", "Processing deletion for meal: $meal")
                    viewModel.deleteMeal(meal.id)
                }
                Toast.makeText(this@MealActivity, "변경 사항이 저장되었습니다.", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@MealActivity, "저장 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}