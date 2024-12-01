package com.example.application.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.application.R
import com.example.application.SessionManager
import com.example.application.databinding.ActivityMainBinding
import com.example.application.ui.main.pages.HealthFragment
import com.example.application.ui.main.pages.LeaderboardFragment
import com.example.application.ui.main.pages.RewardFragment
import com.example.application.ui.main.pages.StoreFragment
import com.example.application.ui.meals.FoodSearchActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    var selectedDate: String = java.time.LocalDate.now().toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.viewPager) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updateLayoutParams<CoordinatorLayout.LayoutParams> {
                bottomMargin = systemBars.bottom
            }
            insets
        }

        savedInstanceState?.let {
            selectedDate = it.getString("selectedDate", selectedDate)
        }


        initUi()
    }

    private fun initUi() = with(binding) {
        viewPager.adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)

        bottomNavigationView.menu.getItem(2).isEnabled = false
        bottomNavigationView.setOnApplyWindowInsetsListener { view, insets ->
            view.updatePadding(bottom = 0)
            insets
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                when (position) {
                    0 -> bottomNavigationView.selectedItemId = R.id.action_health
                    1 -> bottomNavigationView.selectedItemId = R.id.action_leaderboard
                    2 -> bottomNavigationView.selectedItemId = R.id.action_reward
                    else -> bottomNavigationView.selectedItemId = R.id.action_store
                }
            }
        })
        viewPager.isUserInputEnabled = false

        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.action_health -> {
                    viewPager.currentItem = 0
                    return@setOnItemSelectedListener true
                }

                R.id.action_leaderboard -> {
                    viewPager.currentItem = 1
                    return@setOnItemSelectedListener true
                }

                R.id.action_reward -> {
                    viewPager.currentItem = 2
                    return@setOnItemSelectedListener true
                }

                R.id.action_store -> {
                    viewPager.currentItem = 3
                    return@setOnItemSelectedListener true
                }
            }

            return@setOnItemSelectedListener false
        }

        addButton.setOnClickListener {
            Log.d("MainActivity", "Main -> FoodSearch with date: $selectedDate")
            startActivity(Intent(this@MainActivity, FoodSearchActivity::class.java).apply {
                putExtra("date", selectedDate)
            })
        }
    }

    private class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fragmentManager, lifecycle) {
        override fun getItemCount() = 4

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> HealthFragment()
                1 -> LeaderboardFragment()
                2 -> RewardFragment()
                else -> StoreFragment()
            }
        }
    }

    fun updateSelectedDate(date: String) {
        selectedDate = date
        Log.d("MainActivity", "Updated date by HealthFragment: $selectedDate")
    }
}