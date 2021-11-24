package com.example.chart.chart

import com.github.mikephil.charting.formatter.ValueFormatter
import kotlin.math.roundToInt

class ValueFormatter(private val type: YAxisType) : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        val unit = when (type) {
            YAxisType.CONSUMPTION -> "kWh"
            YAxisType.PRICE -> "ø/kWh"
        }
        return "${value.roundToInt()} $unit"
    }
}