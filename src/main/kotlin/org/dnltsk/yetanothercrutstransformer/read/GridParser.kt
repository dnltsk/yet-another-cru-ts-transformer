package org.dnltsk.yetanothercrutstransformer.read

import com.google.inject.Singleton
import org.dnltsk.yetanothercrutstransformer.model.GridPoint
import org.dnltsk.yetanothercrutstransformer.model.GridRef
import org.dnltsk.yetanothercrutstransformer.model.Period
import org.dnltsk.yetanothercrutstransformer.util.batch
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.*
import java.util.regex.Pattern


@Singleton
class GridParser {

    private val LOG = LoggerFactory.getLogger(this::class.java)

    private val FIRST_GRID_BOX_AT_LINE_INDEX = 5

    fun parse(lines: List<String>, period: Period): List<GridPoint> {
        val allGridPoints = mutableListOf<GridPoint>()
        val dataLines = lines.subList(FIRST_GRID_BOX_AT_LINE_INDEX, lines.size)
        val years = (period.fromYear..period.toYear).toList()
        val numLinesPerGridBox = years.size + 1

        dataLines.asSequence()
                .batch(numLinesPerGridBox)
                .forEach { gridBoxLines ->
                    val gridPointsOfGribBox = parseGridBox(
                            gridBoxLines = gridBoxLines,
                            years = years
                    )
                    allGridPoints.addAll(gridPointsOfGribBox)
                }
        return allGridPoints
    }

    fun parseGridBox(gridBoxLines: List<String>, years: List<Int>): List<GridPoint> {
        val gridPointsOfGridBox = mutableListOf<GridPoint>()
        val gridRef = parseGridRef(gridBoxLines.first())
        gridBoxLines.forEachIndexed { index, yearLine ->
            if (index == 0) {
                return@forEachIndexed
            }
            val year = years.get(index - 1)
            val gridPointsOfYear = parseYearLine(
                    yearLine = yearLine,
                    year = year,
                    gridRef = gridRef)
            gridPointsOfGridBox.addAll(gridPointsOfYear)
        }

        return gridPointsOfGridBox
    }

    fun parseYearLine(yearLine: String, year: Int, gridRef: GridRef): List<GridPoint> {
        try {
            val gridPointsOfYear = mutableListOf<GridPoint>()
            val fiveCharLongPattern = ".{5}"
            val p = Pattern.compile(fiveCharLongPattern)
            val m = p.matcher(yearLine)
            var monthIndex = 0
            while (m.find()) {
                val value = m.group().trim().toInt()
                val date = computeInstant(monthIndex, year)
                gridPointsOfYear.add(GridPoint(
                        date = date,
                        gridRef = gridRef,
                        value = value
                ))
                monthIndex++
            }
            return gridPointsOfYear
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