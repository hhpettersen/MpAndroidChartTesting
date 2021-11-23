package com.example.chart

import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.chart.chart.EntryData
import com.example.chart.databinding.ActivityMainBinding
import com.github.mikephil.charting.data.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

//        val startData = listOf(2, 4, 6, 4)
//        val res = DataSetHelper.createThresholdData(startData)
//        println("<<${res}")

//        val lineData = EntryData.randomData(4, 0, 1800)
//        println("<<${lineData}")
//        val res = DataSetHelper.createThresholdData(lineData, 500F)
//        println("<<${res}")

        lineChart1()
        combinedChart()

        /**
         * Attempt overlaping two charts. Left y-axis on linechart and right x-axis on barchart with large difference in values.
         * If it works we can have everything fixed besides setting over threshold to a different color
         * We could use gradient background to give an idication on high/low
         */
    }

    private fun lineChart1() {
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
            color = ContextCompat.getColor(this@MainActivity, R.color.softForest)
        }

        val lData = LineData(lSet1)
        val bData = BarData(bSet1)

        binding.lineChart.apply {
            data = lData
            axisRight.isEnabled = false
        }

        binding.barChart.apply {
            data = bData
            axisLeft.isEnabled = false
        }

        val gradient = LinearGradient(
            0f, 1000f, 0f, 0f,
            ContextCompat.getColor(this, R.color.forest),
            ContextCompat.getColor(this, R.color.flamingoUU),
            Shader.TileMode.CLAMP
        )

        val paint = binding.lineChart.renderer.paintRender
        paint.setShader(gradient)
    }

    //    private fun lineChart2() {
//        val colors: MutableList<Int>
//        val yValue: MutableList<Entry>
//
//        val dataSet = DataSetHelper().getDataSet(10)
//
//        val lineData = getLineData()
//
//        binding.lineChart.apply {
//            data = lineData
//        }
//    }
//
    private fun combinedChart() {
        val startData = EntryData.randomData(10, 0, 1000)
//        println("<<${startData}")
        val avgData = DataSetHelper.createAvgData(startData)
        val thresholdData = DataSetHelper.createThresholdData(startData, 500F)
//        println("<<${thresholdData}")

        val lSet1 = LineDataSet(avgData, "l1").apply {
            lineWidth = 2f
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawFilled(false)
            setDrawCircles(false)
            color = ContextCompat.getColor(this@MainActivity, R.color.teal_700)
        }
        val lSet2 = LineDataSet(thresholdData, "l2").apply {
            lineWidth = 2f
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawFilled(false)
            setDrawCircles(false)
            color = ContextCompat.getColor(this@MainActivity, R.color.red)
        }
//        val bSet1 = BarDataSet(bar1, "b1").apply {
//            color = ContextCompat.getColor(this@MainActivity, R.color.green)
//        }

        val combinedData = LineData(lSet2)

        val entries = DataSetHelper
            .createListOfEntriesOnThreshold(thresholdData, 500F)
            .flatten()

//        combinedData.addDataSet(LineDataSet(entries, ""))

//        val lineDataSets = entries.map {
//            LineDataSet(it, "")
//        }
//
//        lineDataSets.forEach {
//            combinedData.addDataSet(it)
//        }

//        val done = DataSetHelper.createLineDataBasedOnThreshold(thresholdData, 500F)
//        val set = done.getDataSetByLabel("1", false)
//        done.addDataSet(lSet1)
//        done.addDataSet(lSet2)

//        val random = listOf<Entry>(
//            Entry(
//                2F,
//                300F
//            ),
//            Entry(
//                3F,
//                400F
//
//            ),
//            Entry(
//                4F,
//                500F
//            )
//        )
//
//        done.addDataSet(
//            LineDataSet(random, "a")
//        )

        binding.combinedChart.apply {
            data = CombinedData().apply {
                setData(combinedData)
                setData(LineData(LineDataSet(entries, "")))
            }
        }
    }
//
//    private fun getLineData(): LineData {
//        val dataSet = DataSetHelper().getUpAndDown(10, 10, 100)
//        val lineDataSet = LineDataSet(dataSet, "test").apply {
//            lineWidth = 2f
//            mode = LineDataSet.Mode.CUBIC_BEZIER
//            setDrawFilled(false)
//            setDrawCircles(false)
//        }
//        return LineData(lineDataSet).apply {
//            setDrawValues(false)
//        }
//    }
}