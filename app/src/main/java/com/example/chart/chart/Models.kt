package com.example.chart.chart

import android.content.Context
import android.graphics.LinearGradient
import android.graphics.Shader
import androidx.core.content.ContextCompat
import com.example.chart.R
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.renderer.CombinedChartRenderer
import com.github.mikephil.charting.renderer.LineChartRenderer

enum class DashboardChartType {
    CONSUMPTION,
    PRICE,
    COST;

    fun getYAxisType(): YAxisType {
        return when (this) {
            PRICE, COST -> YAxisType.PRICE
            CONSUMPTION -> YAxisType.CONSUMPTION
        }
    }
}

enum class YAxisType {
    CONSUMPTION,
    PRICE
}

class ComparisonBarDataSet(
    val barEntries: List<BarEntry>?,
    private val comparisonEntries: List<Entry>,
    private val barThreshold: Float?,
    private val comparisonThreshold: Float?,
    label: String?,
    private val context: Context
) : BarDataSet(barEntries, label) {
    override fun getEntryIndex(e: BarEntry?): Int {
        return barEntries?.indexOf(e) ?: 0
    }

    /**
     * If both the bar-entry and the comparison-entry has breached their threshold we color the bar red
     */
    override fun getColor(index: Int): Int {
        return if (getEntryForIndex(index).y >= barThreshold ?: 0F && comparisonEntries[index].y >= comparisonThreshold ?: 0F) ContextCompat.getColor(
            context,
            R.color.flamingoUU
        )
        else ContextCompat.getColor(context, R.color.softForest)
    }
}

// Custom class to add gradient to lines in a CombinedChart
class GradientRenderer(private val chart: CombinedChart, context: Context, threshold: Float) : CombinedChartRenderer(chart, chart.animator, chart.viewPortHandler) {
    private val gradientThreshold = 1000F * (1 - threshold)

    private val gradient = LinearGradient(
        0f, gradientThreshold, 0f, 0f,
        ContextCompat.getColor(context, R.color.forest),
        ContextCompat.getColor(context, R.color.red),
        Shader.TileMode.CLAMP
    )

    // Function needs to be called after the data has be assigned to CombinedChart
    fun applyGradient() {
        mRenderers.forEach { renderer ->
            if (renderer is LineChartRenderer) {
                chart.lineData.dataSets.forEach { _ ->
                    renderer.paintRender.shader = gradient
                }
            }
        }
    }
}