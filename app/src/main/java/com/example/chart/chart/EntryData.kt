package com.example.chart.chart

import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import kotlin.random.Random

/**
 * Cast to MutableList<BarEntry> if expecting BarEntries
 */
object EntryData {
    fun random(
        numOfEntries: Int,
        min: Int,
        max: Int,
        isBarEntries: Boolean = false,
        startIndex: Int = 0
    ): MutableList<Entry> {
        val entries = mutableListOf<Entry>()

        var index = if (startIndex != 0) startIndex else 0

        repeat(numOfEntries) {
            entries.add(
                if (isBarEntries) {
                    BarEntry(
                        index.toFloat(),
                        Random.nextInt(min, max).toFloat()
                    )
                } else {
                    Entry(
                        index.toFloat(),
                        Random.nextInt(min, max).toFloat()
                    )
                }

            )
            index++
        }
        repeat(5) {
            if (isBarEntries) {
                BarEntry(
                    index.toFloat(),

                    0F
                )
            } else {
                Entry(
                    index.toFloat(),

                    0F
                )
            }
            index++
        }
        return entries
    }

    /**
     * Cast to MutableList<BarEntry> if expecting BarEntries
     */
    fun fluctuating(
        numOfEntries: Int,
        min: Int,
        max: Int,
        isBarEntries: Boolean = false
    ): MutableList<Entry> {
        val entries = mutableListOf<Entry>()

        repeat(numOfEntries) {
            entries.add(
                if (isBarEntries) {
                    BarEntry(
                        it.toFloat(),
                        if (it % 2 == 0) min.toFloat() else max.toFloat(),
                    )
                } else {
                    Entry(
                        it.toFloat(),
                        if (it % 2 == 0) min.toFloat() else max.toFloat(),
                    )
                }

            )
        }
        return entries
    }
}