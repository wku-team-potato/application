package com.example.application.ui.meals

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.example.application.databinding.ItemFoodInMealsBinding
import com.example.application.ui.meals.function.data.MealResponse

class MealAdapter(
    private val onItemModified: (MealResponse, Int, Int) -> Unit,
    private val onItemRemoved: (MealResponse) -> Unit
) : RecyclerView.Adapter<MealAdapter.MealViewHolder>() {

    private val items = mutableListOf<MealResponse>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemFoodInMealsBinding.inflate(inflater, parent, false)
        return MealViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val item = items[position]
        Log.d("MealAdapter", "Binding item at position: $position with item: $item")
        holder.bind(item, onItemModified) {
            removeItemAt(position, item)
        }
    }

    override fun getItemCount(): Int = items.size

    fun setItems(newItems: List<MealResponse>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    private fun removeItemAt(position: Int, meal: MealResponse) {
        if (position in items.indices) {
            items.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, items.size)
            onItemRemoved(meal)
        }
    }

    class MealViewHolder(private val binding: ItemFoodInMealsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            meal: MealResponse,
            onItemModified: (MealResponse, Int, Int) -> Unit,
            onItemRemoved: () -> Unit
        ) {
            var count = 1
            var quantity = meal.serving_size

            binding.nameTextView.text = meal.food.food_name
            binding.countTextView.text = count.toString()
            binding.quantityEditText.setText(quantity.toString())

            fun updateMeal() {
                onItemModified(meal, count, quantity)
            }

            binding.minusButton.setOnClickListener {
                if (count > 1) {
                    count--
                    binding.countTextView.text = count.toString()
                    updateMeal()
                }
            }

            binding.plusButton.setOnClickListener {
                count++
                binding.countTextView.text = count.toString()
                updateMeal()
            }

            binding.quantityEditText.doAfterTextChanged {
                quantity = it.toString().toIntOrNull() ?: meal.serving_size
                updateMeal()
            }

            binding.removeButton.setOnClickListener {
                onItemRemoved()
            }
        }
    }
}