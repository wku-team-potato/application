package com.example.application.ui.meals

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.application.databinding.ItemFoodInSearchBinding
import com.example.application.ui.meals.function.data.FoodResponse

class FoodAdapter(
    private val onFoodSelected: (FoodResponse?, Boolean) -> Unit
) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    private val foods = mutableListOf<FoodResponse>()
    private var selectedFood: FoodResponse? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemFoodInSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val food = foods[position]
        val isSelected = food == selectedFood
        holder.bind(food, isSelected) { clickedFood ->
            when {
                clickedFood == selectedFood -> {
                    selectedFood = null
                    notifyDataSetChanged()
                    onFoodSelected(null, true)
                }
                selectedFood == null -> {
                    selectedFood = clickedFood
                    notifyDataSetChanged()
                    onFoodSelected(clickedFood, true)
                }
                else -> {
                    onFoodSelected(null, false)
                }
            }
        }
    }

    override fun getItemCount() = foods.size

    fun submitList(newFoods: List<FoodResponse>) {
        foods.clear()
        foods.addAll(newFoods)
        selectedFood = null
        notifyDataSetChanged()
    }

    class FoodViewHolder(private val binding: ItemFoodInSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(food: FoodResponse, isSelected: Boolean, onClick: (FoodResponse) -> Unit) {
            binding.foodNameTextView.text = food.food_name
            binding.root.setBackgroundColor(
                if (isSelected) Color.LTGRAY else Color.TRANSPARENT
            )
            binding.root.setOnClickListener { onClick(food) }
        }
    }
}