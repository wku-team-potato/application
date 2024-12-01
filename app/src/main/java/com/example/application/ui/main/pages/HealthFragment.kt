package com.example.application.ui.main.pages

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.application.R
import com.example.application.RetrofitInstance
import com.example.application.common.extensions.displayText
import com.example.application.databinding.FragmentHealthBinding
import com.example.application.databinding.LayoutCalendarDayBinding
import com.example.application.ui.main.MainActivity
import com.example.application.ui.meals.CalorieActivity
import com.example.application.ui.meals.MealActivity
import com.example.application.ui.meals.function.data.HealthResponse
import com.example.application.ui.meals.function.data.MealSummaryResponse
import com.example.application.ui.meals.function.repository.HealthRepository
import com.example.application.ui.meals.function.repository.MealRepository
import com.example.application.ui.meals.function.viewmodel.HealthViewModel
import com.example.application.ui.meals.function.viewmodel.HealthViewModelFactory
import com.example.application.ui.meals.function.viewmodel.MealViewModel
import com.example.application.ui.meals.function.viewmodel.MealViewModelFactory
import com.example.application.ui.meals.function.viewmodel.SharedMealViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.MPPointF
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.core.atStartOfMonth
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.ViewContainer
import com.kizitonwose.calendar.view.WeekDayBinder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter


class HealthFragment : BaseFragment() {
    private var _binding: FragmentHealthBinding? = null
    private val binding get() = _binding!!
    private lateinit var mealViewModel : MealViewModel
    private lateinit var healthViewModel: HealthViewModel
    private val sharedMealViewModel: SharedMealViewModel by activityViewModels()


    private var selectedDate = MutableStateFlow(LocalDate.now())
    private val dateFormatter = DateTimeFormatter.ofPattern("dd")
    private val apiDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    private val totalProtein = 720.0
    private val totalCarbohydrate = 1200.0
    private val totalFat = 480.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHealthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        val formattedApiDate = selectedDate.value.format(apiDateFormatter)
        Log.d("HealthFragment", "Refreshing data for date: $formattedApiDate")

        lifecycleScope.launch {
            try {
                mealViewModel.loadMealSummary(formattedApiDate)
                mealViewModel.mealSummary.observe(viewLifecycleOwner) { summary ->
                    if (summary != null && summary.summary.calorie > 0.0) {
                        binding.noRecordContainer.isVisible = false
                        updatePieCharts(summary)
                        updateCalorieBar(summary.summary.calorie)
                        Log.d("HealthFragment", "Data successfully updated for date: $formattedApiDate")
                    } else {
                        binding.noRecordContainer.isVisible = true
                        Log.d("HealthFragment", "No data available for date: $formattedApiDate")
                    }
                }
            } catch (e: Exception) {
                Log.e("HealthFragment", "Error while refreshing data: ${e.message}")
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedMealViewModel.refreshTrigger.observe(viewLifecycleOwner) { refresh ->
            if (refresh == true) {
                Log.d("HealthFragment", "Refresh signal received")
                refreshUI()
            }
        }

        initUi()
        val healthRepository = HealthRepository(RetrofitInstance.healthService)
        healthViewModel = ViewModelProvider(this, HealthViewModelFactory(healthRepository))
            .get(HealthViewModel::class.java)
        val mealRepository = MealRepository(RetrofitInstance.mealService)
        val factory = MealViewModelFactory(mealRepository)
        mealViewModel = ViewModelProvider(this, factory).get(MealViewModel::class.java)

        lifecycleScope.launch {
            selectedDate.collectLatest {date ->
                val formattedApiDate = date.format(apiDateFormatter)
                (activity as? MainActivity)?.updateSelectedDate(formattedApiDate)
                mealViewModel.loadMealSummary(formattedApiDate)
                Log.d("HealthFragment", "Selected date for API: $formattedApiDate")

                mealViewModel.mealSummary.observe(viewLifecycleOwner) { summary ->
                    if (summary == null || summary.summary.calorie == 0.0) {
                        binding.noRecordContainer.isVisible = true
                        Log.d("HealthFragment", "No records found for date: $formattedApiDate")
                    } else {
                        binding.noRecordContainer.isVisible = false
                        updatePieCharts(summary)
                        updateCalorieBar(summary.summary.calorie)
                        fetchWeightData()
                        observeWeightData()
                        Log.d("HealthFragment", "Records found for date: $formattedApiDate")
                    }
                }

                binding.calorieContainer.setOnClickListener {
                    Log.d("HealthFragment", "Sending selected date to CalorieActivity: $formattedApiDate")
                    val intent = Intent(requireContext(), CalorieActivity::class.java).apply {
                        putExtra("selectedDate", formattedApiDate)
                    }
                    startActivity(intent)
                }
            }
        }
    }

    private fun initUi() = with(binding) {
        toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.action_my) {
                showMyPage()
            }
            return@setOnMenuItemClickListener true
        }

        initCalendar()

        calorieContainer.setOnClickListener {
            startActivity(Intent(requireContext(), CalorieActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            })
        }

    }

    private fun initCalendar() = with(binding) {
        class DayViewContainer(view: View) : ViewContainer(view) {
            val bind = LayoutCalendarDayBinding.bind(view)
            lateinit var day: WeekDay

            init {
                view.setOnClickListener {
                    if (selectedDate.value != day.date) {
                        val oldDate = selectedDate.value
                        selectedDate.value = day.date
                        calendarView.notifyDateChanged(day.date)
                        oldDate?.let { calendarView.notifyDateChanged(it) }
                    }
                }
            }

            fun bind(day: WeekDay) {
                this.day = day
                bind.exSevenDateText.text = dateFormatter.format(day.date)
                bind.exSevenDayText.text = day.date.dayOfWeek.displayText()

                val colorRes = if (day.date == selectedDate.value) {
                    R.color.md_theme_inversePrimary
                } else {
                    R.color.md_theme_onPrimary
                }
                bind.exSevenDateText.setTextColor(ContextCompat.getColor(view.context, colorRes))
                bind.exSevenSelectedView.isVisible = day.date == selectedDate.value
            }
        }

        calendarView.dayBinder = object : WeekDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: WeekDay) = container.bind(data)
        }

        val currentMonth = YearMonth.now()
        calendarView.setup(
            currentMonth.minusMonths(5).atStartOfMonth(),
            currentMonth.plusMonths(5).atEndOfMonth(),
            firstDayOfWeekFromLocale(),
        )
        calendarView.scrollToDate(LocalDate.now())
    }

    private fun updatePieCharts(summary: MealSummaryResponse) {
        val proteinRatio = (summary.summary.protein / totalProtein) * 100
        val carbohydrateRatio = (summary.summary.carbohydrate / totalCarbohydrate) * 100
        val fatRatio = (summary.summary.fat / totalFat) * 100

        setPieChartData("단백질", binding.proteinChart, proteinRatio)
        setPieChartData("탄수화물", binding.carbohydratesChart, carbohydrateRatio)
        setPieChartData("지방", binding.fatChart, fatRatio)
    }

    private fun updateCalorieBar(calorie: Double) {
        val maxCalorie = 2400.0
        val calorieRatio = (calorie / maxCalorie).coerceAtMost(1.0).toFloat()

        binding.calorieBar.setBackgroundColor(
            if (calorieRatio < 1.0) {
                ContextCompat.getColor(requireContext(), R.color.md_theme_errorContainer_mediumContrast)
            } else {
                ContextCompat.getColor(requireContext(), R.color.md_theme_primary)
            }
        )

        binding.calorieBar.layoutParams = binding.calorieBar.layoutParams.apply {
            width = (binding.calorieContainer.width * calorieRatio).toInt()
        }
    }

    private fun setPieChartData(title: String, chart: PieChart, value: Double) {
        val entries = listOf(
            PieEntry(value.toFloat(), ""),
            PieEntry(100 - value.toFloat(), "")
        )

        val dataSet = PieDataSet(entries, "").apply {
            colors = listOf(
                ContextCompat.getColor(requireContext(), R.color.md_theme_errorContainer_mediumContrast),
                ContextCompat.getColor(requireContext(), R.color.md_theme_primary)
            )
            sliceSpace = 3f
        }

        val data = PieData(dataSet).apply {
            setDrawValues(false)
        }

        chart.apply {
            setUsePercentValues(true)
            legend.isEnabled = false
            description.isEnabled = false

            centerText = title
            setCenterTextSize(18f)
            setDrawCenterText(true)

            isDrawHoleEnabled = true
            setHoleColor(Color.TRANSPARENT)
            setTransparentCircleColor(Color.WHITE)
            setTransparentCircleAlpha(110)
            holeRadius = 75f
            transparentCircleRadius = 80f

            setRotationAngle(0f)
            isRotationEnabled = false
            isHighlightPerTapEnabled = false

            this.data = data
            invalidate()
        }
    }

    private fun fetchWeightData() {
        lifecycleScope.launch {
            healthViewModel.getWeightList()
        }
    }

    private fun observeWeightData() {
        healthViewModel.weightList.observe(viewLifecycleOwner) { list ->
            if (list != null && list.isNotEmpty()) {
                updateLineChart(list)
            } else {
                Log.d("HealthFragment", "No weight data available.")
            }
        }
    }

    private fun updateLineChart(data: List<HealthResponse>) {
        val sortedData = data.sortedBy { it.created_at }

        val dateLabels = sortedData.map { item ->
            item.created_at.substring(5, 10)
        }

        val entries = sortedData.mapIndexed { index, item ->
            Entry(index.toFloat(), item.weight.toFloat())
        }

        val lineDataSet = LineDataSet(entries, "몸무게").apply {
            color = ContextCompat.getColor(requireContext(), R.color.md_theme_primary)
            setCircleColor(ContextCompat.getColor(requireContext(), R.color.md_theme_primary))
            lineWidth = 2f
            circleRadius = 4f
            setDrawCircleHole(false)
            valueTextSize = 10f
        }

        val lineData = LineData(lineDataSet)

        binding.weightChart.apply {
            this.data = lineData

            xAxis.apply {
                isEnabled = true
                valueFormatter = IndexAxisValueFormatter(dateLabels)
                granularity = 1f // 최소 간격 1
                position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
                textColor = Color.BLACK
                setDrawGridLines(false)
            }

            description.isEnabled = false
            axisRight.isEnabled = false
            axisLeft.textColor = Color.BLACK
            legend.isEnabled = false
            invalidate()
        }
    }

    private fun refreshUI() {
        val formattedApiDate = selectedDate.value.format(apiDateFormatter)
        mealViewModel.loadMealSummary(formattedApiDate)
    }
}
