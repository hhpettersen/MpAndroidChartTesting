package com.example.chart

import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import java.util.*

class DataSetHelper {
    fun getDataSet(numOfEntries: Int): List<Entry> {
        val entries = mutableListOf<Entry>()
        repeat(numOfEntries) {
            entries.add(
                Entry(
                    it.toFloat(),
                    getRandomFloat()
                )
            )
        }
        return entries
    }

    private fun getRandomFloat(): Float {
        val min = 10
        val max = 100
        return Random().nextFloat() * (max - min) + min
    }

    fun getUpAndDown(numOfEntries: Int, min: Int, max: Int): List<Entry> {
        val entries = mutableListOf<Entry>()

        repeat(numOfEntries) {
            entries.add(
                Entry(
                    it.toFloat(),
                    if (it % 2 == 0) min.toFloat() else max.toFloat(),
                )
            )
        }
        return entries
    }

    fun getUpAndDownBar(numOfEntries: Int, min: Int, max: Int): List<BarEntry> {
        val entries = mutableListOf<BarEntry>()

        repeat(numOfEntries) {
            entries.add(
                BarEntry(
                    it.toFloat(),
                    if (it % 2 == 0) min.toFloat() else max.toFloat(),
                )
            )
        }
        return entries
    }
}