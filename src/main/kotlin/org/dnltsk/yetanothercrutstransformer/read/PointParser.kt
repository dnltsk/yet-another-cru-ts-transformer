package org.dnltsk.yetanothercrutstransformer.read

import com.google.inject.Singleton
import org.dnltsk.yetanothercrutstransformer.model.GridRef
import org.dnltsk.yetanothercrutstransformer.model.Point
import org.dnltsk.yetanothercrutstransformer.util.batch
import java.time.Instant
import java.util.*


@Singleton
class PointParser {

    private val DATA_BEGINS_AT_LINE_INDEX = 5

    fun parse(lines: List<String>, years: List<Int>): List<Point> {
        val allPoints = mutableListOf<Point>()
        val dataLines = lines.subList(DATA_BEGINS_AT_LINE_INDEX, lines.size)
        val numLinesPerTimeSeries = years.size + 1

        dataLines.asSequence()
                .batch(numLinesPerTimeSeries)
                .forEach { timeSeriesLines ->
                    val pointOfTimeSeries = parseTimeSeries(
                            timeSeriesLines = timeSeriesLines,
                            years = years
                    )
                    allPoints.addAll(pointOfTimeSeries)
                }
        return allPoints
    }

    fun parseTimeSeries(timeSeriesLines: List<String>, years: List<Int>): List<Point> {
        val pointsOfTimeSeries = mutableListOf<Point>()
        val gridRef = parseGridRef(timeSeriesLines.first())
        timeSeriesLines.forEachIndexed { index, yearLine ->
            if (index == 0) {
                return@forEachIndexed
            }
            val year = years.get(index - 1)
            val pointsOfYear = parseYear(
                    yearLine = yearLine,
                    year = year,
                    gridRef = gridRef)
            pointsOfTimeSeries.addAll(pointsOfYear)
        }

        return pointsOfTimeSeries
    }

    fun parseYear(yearLine: String, year: Int, gridRef: GridRef): List<Point> {
        val pointsOfYear = mutableListOf<Point>()
        val splitLine = yearLine.trim().split(Regex("[ ]+"))
        splitLine.forEachIndexed { index, value ->
            val date = computeInstant(index, year)
            pointsOfYear.add(Point(
                    date = date,
                    gridRef = gridRef,
                    value = value.toInt()
            ))
        }
        return pointsOfYear
    }

    private fun computeInstant(index: Int, year: Int): Instant {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.clear()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, index)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        return calendar.toInstant()
    }

    fun parseGridRef(line: String): GridRef {
        val commaNumbers = line.trim().substringAfter("Grid-ref=")
        val splitNumbers = commaNumbers.split(",")
        return GridRef(
                col = splitNumbers[0].trim().toInt(),
                row = splitNumbers[1].trim().toInt()
        )
    }
}