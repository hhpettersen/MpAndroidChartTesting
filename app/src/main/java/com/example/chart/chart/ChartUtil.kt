package com.example.chart.chart

import android.content.Context
import android.graphics.LinearGradient
import android.graphics.Shader
import androidx.core.content.ContextCompat
import com.example.chart.DataSetHelper
import com.example.chart.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.*

object ChartUtil {

    fun lineOnTopOfBarChart(lineChart: LineChart, barChart: BarChart, context: Context) {
        val lineData = EntryData.randomData(24, 500, 1800)
        val barData = EntryData.randomData(24, 10, 100, true) as MutableList<BarEntry>

        val lSet1 = LineDataSet(lineData, "l1").apply {
            lineWidth = 2f
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawFilled(false)
            setDrawCircles(false)
            setDrawValues(false)
        }
        val bSet1 = BarDataSet(barData, "b1").apply {
            color = ContextCompat.getColor(context, R.color.softForest)
        }

        val lData = LineData(lSet1)
        val bData = BarData(bSet1)

        lineChart.apply {
            data = lData
            axisRight.isEnabled = false
        }

        barChart.apply {
            data = bData
            axisLeft.isEnabled = false
        }

        val gradient = LinearGradient(
            0f, 1000f, 0f, 0f,
            ContextCompat.getColor(context, R.color.forest),
            ContextCompat.getColor(context, R.color.flamingoUU),
            Shader.TileMode.CLAMP
        )

        val paint = lineChart.renderer.paintRender
        paint.setShader(gradient)
    }

    /**
     * Chart where the color changes value goes below or above threshold
     */
    fun thresholdColorChangeChart(chart: LineChart, context: Context) {
        val entry = EntryData.randomData(24, 0, 1500)
        val lineDataSets = DataSetHelper.createListOfEntriesOnThreshold(entry, 750F)


        lineDataSets.forEachIndexed { index, set ->
            val c = if (index % 2 == 0) R.color.teal_700 else R.color.red
            set.apply {
                color = ContextCompat.getColor(context, c)
                mode = LineDataSet.Mode.CUBIC_BEZIER
                setDrawFilled(true)
                setDrawCircles(false)
                setDrawHorizontalHighlightIndicator(false)
                lineWidth = 2f
                setDrawValues(false)
            }
        }

        val lineData = LineData()

        lineDataSets.forEach { set ->
            lineData.addDataSet(set)
        }

        val limitLine = LimitLine(750F, "limitline").apply {
            label = ""
            lineWidth = 2f
            lineColor = ContextCompat.getColor(context, R.color.black)
        }

        chart.apply {
            data = lineData
            axisLeft.apply {
                addLimitLine(limitLine)
                setDrawAxisLine(false)
                setDrawGridLines(false)
                setDrawLabels(false)
            }
            axisRight.apply {
                setDrawAxisLine(false)
                setDrawGridLines(false)
                isGranularityEnabled = true

            }
            legend.isEnabled = false
            description.isEnabled = false
            setDrawGridBackground(false)

            xAxis.apply {
                isGranularityEnabled = true
                setDrawAxisLine(false)
                setDrawGridLines(false)
            }
        }
    }
}