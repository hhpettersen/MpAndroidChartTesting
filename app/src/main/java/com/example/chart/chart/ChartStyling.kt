package com.example.chart.chart

import android.content.Context
import androidx.core.content.ContextCompat
import com.example.chart.R
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.LineDataSet


fun LineDataSet.lineDataStyling() {
    lineWidth = 2f
    mode = LineDataSet.Mode.CUBIC_BEZIER
    setDrawFilled(false)
    setDrawCircles(false)
    setDrawValues(false)
}

fun LimitLine.thresholdStyling(context: Context) {
    label = ""
    lineWidth = 1f
    lineColor = ContextCompat.getColor(context, R.color.sun)
}
