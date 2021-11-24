package com.example.chart

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.chart.chart.ChartUtil.lineOnTopOfBarChart
import com.example.chart.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

//        thresholdColorChangeChart(binding.lineChart, this)
        lineOnTopOfBarChart(binding.lineChart, binding.barChart, this)
    }
}
