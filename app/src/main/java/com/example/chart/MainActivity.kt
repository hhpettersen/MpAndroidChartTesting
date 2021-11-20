package com.example.chart

import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.chart.databinding.ActivityMainBinding
import com.github.mikephil.charting.data.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        lineChart1()
        combinedChart()
    }

    private fun lineChart1() {
        val lineData = getLineData()

        binding.lineChart.apply {
            data = lineData
        }

        val gradient = LinearGradient(
            0f, 500f, 0f, 0f,
            ContextCompat.getColor(this, R.color.green),
            ContextCompat.getColor(this, R.color.red),
            Shader.TileMode.CLAMP)

        val paint = binding.lineChart.renderer.paintRender
        paint.setShader(gradient)
    }

    private fun lineChart2() {
        val colors: MutableList<Int>
        val yValue: MutableList<Entry>

        val dataSet = DataSetHelper().getDataSet(10)

        val lineData = getLineData()

        binding.lineChart.apply {
            data = lineData
        }
    }

    private fun combinedChart() {
        val line1 = DataSetHelper().getUpAndDown(10, 10, 20)
        val line2 = DataSetHelper().getUpAndDown(5, 500, 1000)
        val bar1 = DataSetHelper().getUpAndDownBar(10, 500, 800)

        val lSet1 = LineDataSet(line1, "l1").apply {
            lineWidth = 2f
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawFilled(false)
            setDrawCircles(false)
            color = ContextCompat.getColor(this@MainActivity, R.color.teal_700)
        }
        val lSet2 = LineDataSet(line2, "l2").apply {
            lineWidth = 2f
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawFilled(false)
            setDrawCircles(false)
            color = ContextCompat.getColor(this@MainActivity, R.color.red)
        }
        val bSet1 = BarDataSet(bar1, "b1").apply {
            color = ContextCompat.getColor(this@MainActivity, R.color.green)
        }

        val data1 = LineData(lSet1, lSet2)
        val data2 = BarData(bSet1)

        binding.combinedChart.apply {
            data = CombinedData().apply {
                setData(data1)
                setData(data2)
            }
        }
    }

    private fun getLineData(): LineData {
        val dataSet = DataSetHelper().getUpAndDown(10, 10, 100)
        val lineDataSet = LineDataSet(dataSet, "test").apply {
            lineWidth = 2f
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawFilled(false)
            setDrawCircles(false)
        }
        return LineData(lineDataSet).apply {
            setDrawValues(false)
        }
    }
}