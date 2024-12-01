package com.example.application.ui.meals

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.application.databinding.ItemCalorieBinding

class CalorieAdapter(
    private val mealList: List<String>,
    private val calorieData: List<Int>
) : RecyclerView.Adapter<CalorieAdapter.MealViewHolder>() {

    var onItemClickListener: ((String) -> Unit)? = null

    class MealViewHolder(private val binding: ItemCalorieBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mealType: String, calorie: Int, onClick: ((String) -> Unit)?) {
            binding.mealtimeTextView.text = mealType
            binding.calorieTextView.text = "총 칼로리 ${calorie}kcal"
            binding.root.setOnClickListener { onClick?.invoke(mealType) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val binding = ItemCalorieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MealViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        holder.bind(mealList[position], calorieData[position], onItemClickListener)
    }

    override fun getItemCount() = mealList.size
}