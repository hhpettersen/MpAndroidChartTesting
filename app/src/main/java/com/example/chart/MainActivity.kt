package com.example.chart

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.chart.chart.ChartUtil.dashBoardChart
import com.example.chart.chart.DashboardChartType
import com.example.chart.chart.EntryData
import com.example.chart.databinding.ActivityMainBinding
import com.github.mikephil.charting.data.BarEntry


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val barEntries = EntryData.randomData(24, 1000, 5000, true) as MutableList<BarEntry>
        val lineEntries = EntryData.randomData(24, 80, 150)

        dashBoardChart(
            combinedChart = binding.combinedChart,
            consumptionData = barEntries,
            priceData = lineEntries,
            thresholdPercentage = 0.7F,
            type = DashboardChartType.CONSUMPTION,
            context = this,
        )

        dashBoardChart(
            combinedChart = binding.combinedChart1,
            consumptionData = barEntries,
            priceData = lineEntries,
            thresholdPercentage = 0.7F,
            type = DashboardChartType.PRICE,
            context = this,
        )

        dashBoardChart(
            combinedChart = binding.combinedChart2,
            consumptionData = barEntries,
            priceData = lineEntries,
            thresholdPercentage = 0.7F,
            type = DashboardChartType.COST,
            context = this,
        )
    }
}
