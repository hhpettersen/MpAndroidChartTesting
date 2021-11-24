package com.example.chart

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet

object DataSetHelper {

    fun createListOfEntriesOnThreshold(
        entries: List<Entry>,
        threshold: Float
    ): List<LineDataSet> {
        val transformedEntries = createThresholdData(entries, threshold)

        val lineDataSets = mutableListOf<LineDataSet>()

        var lastSliced = 0
        transformedEntries.forEachIndexed { index, entry ->
            if (entry.y == threshold) {
                val subList = transformedEntries.subList(lastSliced, index + 1)
                val lineDataSet = LineDataSet(subList, "")
                lineDataSets.add(lineDataSet)
                lastSliced = index
            }
        }

        return lineDataSets
    }

    /**
     * 2, 4, 6, 4
     * 2, 3, 4, 5, 6, 5, 4
     *
     *
     */
    private fun createThresholdData(entries: List<Entry>, threshold: Float): List<Entry> {
        var entryIndex = 0

        val newList = mutableListOf<Entry>()

        val size = entries.size - 1

        entries.forEachIndexed { index, entry ->
            // Use to set correct index to entry when added to list
            // Add every entry
            newList.add(
                Entry(
                    entryIndex.toFloat(),
                    entry.y
                )
            )
            entryIndex++

            // If not the last entry in list, should create new Entry based on either avg or threshold
            if (index != size) {
                val currentY = entry.y
                val nextY = entries[index + 1].y

                val avg = (currentY + nextY) / 2

                val newY =
                    if ((currentY < threshold && nextY > threshold) || (currentY > threshold && nextY < threshold)) threshold else avg

                newList.add(
                    Entry(
                        entryIndex.toFloat(),
                        newY
                    )
                )
                entryIndex++
            }
        }

        return newList
    }

    private fun createAvgData(entries: List<Entry>): List<Entry> {
        var entryIndex = 0

        val newList = mutableListOf<Entry>()

        val size = entries.size - 1

        entries.forEachIndexed { index, entry ->
            // Use to set correct index to entry when added to list
            // Add every entry
            newList.add(
                Entry(
                    entryIndex.toFloat(),
                    entry.y
                )
            )
            entryIndex++

            // If not the last entry in list, should create new Entry based on either avg or threshold
            if (index != size) {
                val currentY = entry.y
                val nextY = entries[index + 1].y

                val avg = (currentY + nextY) / 2

                newList.add(
                    Entry(
                        entryIndex.toFloat(),
                        avg
                    )
                )
                entryIndex++
            }
        }

        return newList
    }
}