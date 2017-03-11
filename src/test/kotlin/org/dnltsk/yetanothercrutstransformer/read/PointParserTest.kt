package org.dnltsk.yetanothercrutstransformer.read

import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.dnltsk.yetanothercrutstransformer.GoldenTestData
import org.dnltsk.yetanothercrutstransformer.model.GridRef
import org.dnltsk.yetanothercrutstransformer.model.Point
import org.junit.Test
import java.time.Instant

class PointParserTest {

    private val parser = PointParser()

    @Test
    fun gridRef_is_parsed_correctly() {
        val gridRef = parser.parseGridRef("Grid-ref= 116, 263")
        val expectedGridRef = GridRef(col = 116, row = 263)
        assertThat(gridRef).isEqualTo(expectedGridRef)
    }

    @Test
    fun gridRef_with_single_number_is_parsed_correctly() {
        val gridRef = parser.parseGridRef("Grid-ref=   1, 148")
        val expectedGridRef = GridRef(col = 1, row = 148)
        assertThat(gridRef).isEqualTo(expectedGridRef)
    }

    @Test
    fun year_line_is_parsed_correctly() {
        val pointOfYear = parser.parseYear(
                yearLine = "2660 1003 1226 1689  775  598    5  181    8  321  261 1240",
                year = 2017,
                gridRef = GridRef(col = 116, row = 263))
        val expectedJanuaryPoint = Point(
                date = Instant.parse("2017-01-01T00:00:00Z"),
                gridRef = GridRef(col = 116, row = 263),
                value = 2660
        )
        val expectedJulyPoint = Point(
                date = Instant.parse("2017-07-01T00:00:00Z"),
                gridRef = GridRef(col = 116, row = 263),
                value = 5
        )
        val expectedDecemberPoint = Point(
                date = Instant.parse("2017-12-01T00:00:00Z"),
                gridRef = GridRef(col = 116, row = 263),
                value = 1240
        )
        assertThat(pointOfYear.first()).isEqualTo(expectedJanuaryPoint)
        assertThat(pointOfYear.get(6)).isEqualTo(expectedJulyPoint)
        assertThat(pointOfYear.last()).isEqualTo(expectedDecemberPoint)
    }

    @Test
    fun timeSeries_is_parsed_correctly() {
        val timeSeriesLines = listOf(
                "Grid-ref= 116, 266",
                "499  405 1593  582  729  214  335  383    8  494 1501  807",
                "498  361  392  691   98  558  271   68  253  691  684 1524",
                "1321  442  995 1016  760  508  307  527    3  449  332 1028"
        )
        val pointsOfTimeSeries = parser.parseTimeSeries(
                timeSeriesLines = timeSeriesLines,
                years = listOf(2000, 1985, 2017)
        )
        val expectedFirstPoint = Point(
                date = Instant.parse("2000-01-01T00:00:00Z"),
                gridRef = GridRef(col = 116, row = 266),
                value = 499
        )
        val expectedMiddlePoint = Point(
                date = Instant.parse("1985-06-01T00:00:00Z"),
                gridRef = GridRef(col = 116, row = 266),
                value = 558
        )
        val expectedLastPoint = Point(
                date = Instant.parse("2017-12-01T00:00:00Z"),
                gridRef = GridRef(col = 116, row = 266),
                value = 1028
        )
        assertThat(pointsOfTimeSeries.first()).isEqualTo(expectedFirstPoint)
        assertThat(pointsOfTimeSeries.get(17)).isEqualTo(expectedMiddlePoint)
        assertThat(pointsOfTimeSeries.last()).isEqualTo(expectedLastPoint)
    }

    @Test
    fun sampleFile_is_parsed_correctly() {
        val allPoints = parser.parse(
                lines = GoldenTestData.sampleCruTsPreLines(),
                years = (1991..2000).toList()
        )
        val expectedFirstPoint = Point(
                date = Instant.parse("1991-01-01T00:00:00Z"),
                gridRef = GridRef(col = 1, row = 148),
                value = 3020
        )
        val expectedLastPoint = Point(
                date = Instant.parse("2000-12-01T00:00:00Z"),
                gridRef = GridRef(col = 142, row = 268),
                value = 272
        )
        assertThat(allPoints.first()).isEqualTo(expectedFirstPoint)
        assertThat(allPoints.last()).isEqualTo(expectedLastPoint)
    }
}