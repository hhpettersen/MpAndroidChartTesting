package com.example.chart.chart

import android.content.Context
import android.graphics.LinearGradient
import android.graphics.Shader
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.doOnLayout
import com.example.chart.DataSetHelper
import com.example.chart.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.charts.CombinedChart.DrawOrder
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.*
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.renderer.CombinedChartRenderer
import com.github.mikephil.charting.renderer.LineChartRenderer
import java.util.*


object ChartUtil {

    /**
     * All values intraday 00 - 24
     *
     * Only LEFT Y-axis shown
     *
     * 1)
     * Barchart - green
     * Y-axis: kWh
     *
     * 2)
     * Linechart - gradient (green/red) - stippled after current hour
     * Limitline - yellow - 70% of max linechart
     * Y-axis: ø/kWh
     *
     * 3)
     * Barchart - green/red (red if consumption both consumtion and price has breached threshold at 70%)
     * Linechart - gradient (green/red) - stippled after current hour
     * Limitline - yellow - 70% of max linechart
     * Y-axis: ø/kWh
     */

    /**
     * thresholdPercentage: 0F(0%) - 1F(100%)
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

        fun getThreshold(entries: List<Entry>): Float {
            return (entries.map { it.y }.maxOrNull() ?: 0F) * thresholdPercentage
        }

        fun getThresholdNotZero(entries: List<Entry>): Float {
            val max = entries.map { it.y }.maxOrNull() ?: 0F
            val min = entries.map { it.y }.minOrNull() ?: 0F
            return ((max - min) * thresholdPercentage) + min
        }

        when (type) {
            DashboardChartType.CONSUMPTION -> {
                val set = BarDataSet(consumptionData, "").apply {
                    color = ContextCompat.getColor(context, R.color.softForest)
                    setDrawValues(false)
                }
                barData = BarData(set)
            }
            DashboardChartType.PRICE -> {
                limitLine = LimitLine(getThresholdNotZero(priceData), "limitline").apply {
                    label = ""
                    lineWidth = 1f
                    lineColor = ContextCompat.getColor(context, R.color.sun)
                }

                val lineDataSet = LineDataSet(priceData, "").apply {
                    color = ContextCompat.getColor(context, R.color.forest)
                    lineWidth = 2f
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                    setDrawFilled(false)
                    setDrawCircles(false)
                    setDrawValues(false)
                }

                lineData = LineData(lineDataSet)
            }
            DashboardChartType.COST -> {
                val thresholdBar = getThreshold(consumptionData)
                val thresholdLine = getThresholdNotZero(priceData)

                val barDataSet =
                    ComparisonBarDataSet(
                        barEntries = consumptionData,
                        comparisonEntries = priceData,
                        barThreshold = thresholdBar,
                        comparisonThreshold = thresholdLine,
                        label = "",
                        context = context
                    ).apply {
                        setColors()
                        axisDependency = YAxis.AxisDependency.RIGHT
                        setDrawValues(false)
                    }

                val lineDataSet = LineDataSet(priceData, "").apply {
                    axisDependency = YAxis.AxisDependency.LEFT
                    color = ContextCompat.getColor(context, R.color.forest)
                    lineWidth = 2f
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                    setDrawFilled(false)
                    setDrawCircles(false)
                    setDrawValues(false)
                }

                barData = BarData(barDataSet)
                lineData = LineData(lineDataSet)

                limitLine = LimitLine(thresholdBar, "limitline").apply {
                    label = ""
                    lineWidth = 1f
                    lineColor = ContextCompat.getColor(context, R.color.sun)
                }
            }
        }

        val gradient = LinearGradient(
            0f, 500F, 0f, 0f,
            ContextCompat.getColor(context, R.color.forest),
            ContextCompat.getColor(context, R.color.flamingoUU),
            Shader.TileMode.CLAMP
        )

        combinedChart.renderer.paintRender.shader = gradient


        combinedChart.apply {
            val customRenderer = GradientRenderer(this, context, thresholdPercentage)
            renderer = customRenderer
            data = CombinedData().apply {
                setData(barData)
                setData(lineData)
            }
            customRenderer.applyGradient()

            axisRight.apply {
                limitLine?.let { addLimitLine(it) }
                setDrawLabels(false)
                setDrawGridLines(false)
                if (EnumSet.of(DashboardChartType.COST, DashboardChartType.CONSUMPTION).contains(type)) axisMinimum = 0F
            }
            axisLeft.apply {
                valueFormatter = ValueFormatter(type.getYAxisType())
                setDrawGridLines(false)
                if (EnumSet.of(DashboardChartType.CONSUMPTION).contains(type)) axisMinimum = 0F
            }
            setTouchEnabled(true)
            isDragEnabled = true
            description.isEnabled = false
            setScaleEnabled(false)
            setPinchZoom(false)
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

    fun applyLinearGradient(lineChart: LineChart, context: Context) {
        val gradient = LinearGradient(
            0f, 500F, 0f, 0f,
            ContextCompat.getColor(context, R.color.forest),
            ContextCompat.getColor(context, R.color.flamingoUU),
            Shader.TileMode.CLAMP
        )

        val paint = lineChart.renderer.paintRender
        paint.shader = gradient
    }

    fun combinedChart(combinedChart: CombinedChart, context: Context) {
        val lineData = EntryData.randomData(24, 70, 150)
        val lineData2 = EntryData.randomData(24, 1000, 5000)
        val barData = EntryData.randomData(24, 1000, 5000, true) as MutableList<BarEntry>

        val mChart = combinedChart
        mChart.getDescription().setEnabled(false)
//        mChart.setBackgroundColor(Color.WHITE)
        mChart.setDrawGridBackground(false)
        mChart.setDrawBarShadow(false)
        mChart.setHighlightFullBarEnabled(false)

        mChart.setDrawOrder(
            arrayOf(
                DrawOrder.BAR, DrawOrder.BUBBLE, DrawOrder.CANDLE, DrawOrder.LINE, DrawOrder.SCATTER
            )
        )

        val l: Legend = mChart.getLegend()
        l.isWordWrapEnabled = true
        l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.setDrawInside(false)

        val rightAxis: YAxis = mChart.getAxisRight()
        rightAxis.setDrawGridLines(false)
        rightAxis.granularity = 10f
        rightAxis.axisMinimum = 0f

        val leftAxis: YAxis = mChart.getAxisLeft()
        leftAxis.setDrawGridLines(false)
        rightAxis.granularity = 1f
        leftAxis.axisMinimum = 0f

        val xAxis: XAxis = mChart.getXAxis()
        xAxis.position = XAxisPosition.BOTH_SIDED
        xAxis.axisMinimum = 0f
        xAxis.granularity = 1f
//        xAxis.setValueFormatter(IAxisValueFormatter { value, axis -> mMonths.get(value.toInt() % mMonths.length) })

        val data = CombinedData()

        val lSet = LineDataSet(lineData, "")
        val lSet2 = LineDataSet(lineData2, "")
        val bSet = BarDataSet(barData, "")

        lSet.apply {
            axisDependency = YAxis.AxisDependency.LEFT
            lineWidth = 2f
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawFilled(false)
            setDrawCircles(false)
            setDrawValues(false)
            color = ContextCompat.getColor(context, R.color.flamingoUU)
        }

        lSet2.apply {
            axisDependency = YAxis.AxisDependency.RIGHT
            lineWidth = 1f
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawFilled(false)
            setDrawCircles(false)
            setDrawValues(false)
            color = ContextCompat.getColor(context, R.color.black)
        }

        bSet.apply {
            axisDependency = YAxis.AxisDependency.RIGHT
            color = ContextCompat.getColor(context, R.color.softForest)
        }

//        val lData2 = LineData(lSet2)
        val lData = LineData(lSet)
        lData.addDataSet(lSet2)
        val bData = BarData(bSet)

        data.setData(lData)
//        data.setData(lData2)
        data.setData(bData)

        xAxis.axisMaximum = data.xMax + 0.25f

        mChart.setData(data)
        mChart.invalidate()
    }

    fun lineOnTopOfBarChart(
        lineChart: LineChart,
        barChart: BarChart,
        context: Context,
        lineChart2: LineChart,
        view: ConstraintLayout
    ) {
        val lineData = EntryData.upAndDownData(24, 500, 1800)
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

        val lineDataValues = lineData.map { it.y }
        val avg = lineDataValues.map { it }.average().toFloat()
        val percentage = ((lineDataValues.maxOrNull()?.toInt() ?: 0) * 0.8).toFloat()

        val limitLine = LimitLine(percentage, "limitline").apply {
            label = ""
            lineWidth = 2f
            lineColor = ContextCompat.getColor(context, R.color.black)
        }

        lineChart.apply {
            data = lData
            axisRight.isEnabled = false
            axisLeft.apply {
                addLimitLine(limitLine)
            }
        }

        lineChart2.apply {
            data = lData
            axisRight.isEnabled = false
            axisLeft.apply {
                addLimitLine(limitLine)
            }
        }

        barChart.apply {
            data = bData
            axisLeft.isEnabled = false
        }


        view.doOnLayout {
            val h1 = lineChart.height
            val h2 = lineChart2.height

            val g1 = LinearGradient(
                0f, h1.toFloat(), 0f, 0f,
                ContextCompat.getColor(context, R.color.forest),
                ContextCompat.getColor(context, R.color.flamingoUU),
                Shader.TileMode.CLAMP
            )

            val g2 = LinearGradient(
                0f, h2.toFloat(), 0f, 0f,
                ContextCompat.getColor(context, R.color.forest),
                ContextCompat.getColor(context, R.color.flamingoUU),
                Shader.TileMode.CLAMP
            )

            val p1 = lineChart.renderer.paintRender
            p1.shader = g1
            val p2 = lineChart2.renderer.paintRender
            p2.shader = g2
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
        return if (getEntryForIndex(index).y > barThreshold ?: 0F && comparisonEntries[index].y > comparisonThreshold ?: 0F) ContextCompat.getColor(
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