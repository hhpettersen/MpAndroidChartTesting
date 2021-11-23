package com.example.chart

import android.content.Context
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.data.*
import java.util.*

object DataSetHelper {

    val random = listOf<Entry>(
        Entry(
            2F,
            300F
        ),
        Entry(
            3F,
            400F

        ),
        Entry(
            4F,
            500F
        )
    )

    fun createListOfEntriesOnThreshold(entries: List<Entry>, threshold: Float): List<List<Entry>> {
        var listOfEntries = mutableListOf<List<Entry>>()
        val currentEntryList = mutableListOf<Entry>()

        entries.forEach { entry ->
            currentEntryList.add(entry)
            if (entry.y == threshold) {
                listOfEntries.add(currentEntryList)
                currentEntryList.clear()
                currentEntryList.add(entry)
            }
        }
        return listOfEntries
    }

    fun createLineDataBasedOnThreshold(
        entries: List<Entry>,
        threshold: Float
    ): LineData {
        println("<<${entries}")
        var index = 0
        val lineData = LineData()
        val lineDataSets = mutableListOf<LineDataSet>()
        val entryList = mutableListOf<Entry>()

        entries.forEach { entry ->
            val fakeEntry = Entry(index.toFloat(), 200F)
            index++
            entryList.add(fakeEntry)
            if (entry.y == threshold) {
                println("<<${entryList}")
                val lineDataSet = LineDataSet(entryList, "${lineData.dataSetCount}")
                lineData.addDataSet(lineDataSet)
                entryList.clear()
                entryList.add(fakeEntry)
            }
        }
        return lineData
    }

    /**
     * 2, 4, 6, 4
     * 2, 3, 4, 5, 6, 5, 4
     *
     *
     */
    fun createThresholdData(entries: List<Entry>, threshold: Float): List<Entry> {
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

    fun createAvgData(entries: List<Entry>): List<Entry> {
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

//class LineDataSetOwn(yVals: MutableList<Entry>?, label: String?) : LineDataSet(yVals, label) {
//    override fun getColor(): Int {
//        return super.getColor()
//        if (getEntryForXValue())
//    }
//}