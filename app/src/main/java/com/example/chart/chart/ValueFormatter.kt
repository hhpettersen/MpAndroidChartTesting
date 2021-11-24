package com.example.chart.chart

import com.github.mikephil.charting.formatter.ValueFormatter
import kotlin.math.roundToInt

class ValueFormatter() : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return "${value.roundToInt()} ø/kWh"
    }
}