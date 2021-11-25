package com.example.chart.chart

import android.content.Context
import androidx.core.content.ContextCompat
import com.example.chart.DataSetHelper
import com.example.chart.R
import com.example.chart.chart.DashboardChartType.*
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import java.util.*


object ChartUtil {

    private fun getThreshold(
        entries: List<Entry>,
        thresholdPercentage: Float,
        startZero: Boolean
    ): Float {
        return if (startZero) {
            (entries.map { it.y }.maxOrNull() ?: 0F) * thresholdPercentage
        } else {
            val max = entries.map { it.y }.maxOrNull() ?: 0F
            val min = entries.map { it.y }.minOrNull() ?: 0F
            ((max - min) * thresholdPercentage) + min
        }
    }

    /**
     * thresholdPercentage: 0F(0%) - 1F(100%)
     * All values intra-day 00 - 24
     *
     * TODO
     * always show 24h even if dataset isnt complete
     */
    fun dashBoardChart(
        combinedChart: CombinedChart,
        consumptionData: List<BarEntry>,
        priceData: List<Entry>,
        thresholdPercentage: Float,
        type: DashboardChartType,
        context: Context
    ) {
        var barData: BarData? = null
        var lineData: LineData? = null
        var limitLine: LimitLine? = null

        fun setUpConsumptionChart() {
            val set = BarDataSet(consumptionData, "consumption").apply {
                color = ContextCompat.getColor(context, R.color.softForest)
                setDrawValues(false)
            }
            barData = BarData(set)
        }

        fun setUpPriceChart() {
            limitLine = LimitLine(
                getThreshold(priceData, thresholdPercentage, false),
                "threshold"
            ).also { it.thresholdStyling(context) }

            val lineDataSet = LineDataSet(priceData, "price").also { it.lineDataStyling() }

            lineData = LineData(lineDataSet)
        }

        fun setUpCostChart() {
            val thresholdBar = getThreshold(consumptionData, thresholdPercentage, true)
            val thresholdLine = getThreshold(priceData, thresholdPercentage, false)

            val barDataSet =
                ComparisonBarDataSet(
                    barEntries = consumptionData,
                    comparisonEntries = priceData,
                    barThreshold = thresholdBar,
                    comparisonThreshold = thresholdLine,
                    label = "consumption",
                    context = context
                ).apply {
                    setColors()
                    axisDependency = YAxis.AxisDependency.RIGHT
                    setDrawValues(false)
                }


            val lineDataSet = LineDataSet(priceData, "price").apply {
                axisDependency = YAxis.AxisDependency.LEFT
            }.also {
                it.lineDataStyling()
            }

            barData = BarData(barDataSet)
            lineData = LineData(lineDataSet)
            limitLine = LimitLine(
                thresholdBar,
                "threshold"
            ).also { it.thresholdStyling(context) }
        }

        when (type) {
            CONSUMPTION -> setUpConsumptionChart()
            PRICE -> setUpPriceChart()
            COST -> setUpCostChart()
        }

        /**
         * If we need to add multiple lineDataSets to a combinedChart it can be done in the follow way:
         * {lineDataSet1}, {lineDataSet2} -> val LineData(lineDataSet1) -> lineData.addDataSet(lineDataSet2) -> setData(lineData)
         */
        combinedChart.apply {
            val customRenderer = GradientRenderer(this, context, thresholdPercentage)
            renderer = customRenderer
            data = CombinedData().apply {
                setData(barData)
                setData(lineData)
            }
            customRenderer.applyGradient()

            axisRight.apply {
                limitLine?.let { line ->
                    addLimitLine(line)
                    setDrawLabels(false)
                    setDrawGridLines(false)
                    if (EnumSet.of(COST, CONSUMPTION).contains(type)) axisMinimum = 0F
                }
            }
            axisLeft.apply {
                valueFormatter = ValueFormatter(type.getYAxisType())
                setDrawGridLines(false)
                if (EnumSet.of(CONSUMPTION).contains(type)) axisMinimum = 0F
            }
            setTouchEnabled(true)
            description.isEnabled = false
            setScaleEnabled(false)
            setPinchZoom(false)
            setTouchEnabled(false)
            setDrawGridBackground(false)
            legend.isEnabled = false

            xAxis.apply {
                axisMinimum = data.xMin - .5f
                axisMaximum = data.xMax + .5f
                isGranularityEnabled = true
                setDrawAxisLine(false)
                setDrawGridLines(false)
                position = XAxisPosition.BOTTOM
                labelCount = labelCount
            }
        }
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

