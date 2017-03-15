package org.dnltsk.yetanothercrutstransformer.read

import com.google.inject.Singleton
import org.dnltsk.yetanothercrutstransformer.model.GridRef
import org.dnltsk.yetanothercrutstransformer.model.Period
import org.dnltsk.yetanothercrutstransformer.model.Point
import org.dnltsk.yetanothercrutstransformer.util.batch
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.*
import java.util.regex.Pattern


@Singleton
class GridParser {

    private val LOG = LoggerFactory.getLogger(this::class.java)

    private val FIRST_GRID_BOX_AT_LINE_INDEX = 5

    fun parse(lines: List<String>, period: Period): List<Point> {
        val allPoints = mutableListOf<Point>()
        val dataLines = lines.subList(FIRST_GRID_BOX_AT_LINE_INDEX, lines.size)
        val years = (period.fromYear..period.toYear).toList()
        val numLinesPerGridBox = years.size + 1

        dataLines.asSequence()
                .batch(numLinesPerGridBox)
                .forEach { gridBoxLines ->
                    val pointOfGribBox = parseGridBox(
                            gridBoxLines = gridBoxLines,
                            years = years
                    )
                    allPoints.addAll(pointOfGribBox)
                }
        return allPoints
    }

    fun parseGridBox(gridBoxLines: List<String>, years: List<Int>): List<Point> {
        val pointsOfGridBox = mutableListOf<Point>()
        val gridRef = parseGridRef(gridBoxLines.first())
        gridBoxLines.forEachIndexed { index, yearLine ->
            if (index == 0) {
                return@forEachIndexed
            }
            val year = years.get(index - 1)
            val pointsOfYear = parseYearLine(
                    yearLine = yearLine,
                    year = year,
                    gridRef = gridRef)
            pointsOfGridBox.addAll(pointsOfYear)
        }

        return pointsOfGridBox
    }

    fun parseYearLine(yearLine: String, year: Int, gridRef: GridRef): List<Point> {
        try {
            val pointsOfYear = mutableListOf<Point>()
            val fiveCharLongPattern = ".{5}"
            val p = Pattern.compile(fiveCharLongPattern)
            val m = p.matcher(yearLine)
            var monthIndex = 0
            while (m.find()) {
                val value = m.group().trim().toInt()
                val date = computeInstant(monthIndex, year)
                pointsOfYear.add(Point(
                        date = date,
                        gridRef = gridRef,
                        value = value
                ))
                monthIndex++
            }
            return pointsOfYear
        }catch(e: Throwable){
            LOG.error("cannot parse line with yearly data: $yearLine")
            throw e
        }
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
        return GridRef(
                col = line.substring(9, 13).trim().toInt(),
                row = line.substring(14, 18).trim().toInt()
        )
    }
}