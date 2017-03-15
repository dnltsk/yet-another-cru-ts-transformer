package org.dnltsk.yetanothercrutstransformer.read

import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.dnltsk.yetanothercrutstransformer.GoldenTestData
import org.dnltsk.yetanothercrutstransformer.model.GridRef
import org.dnltsk.yetanothercrutstransformer.model.Period
import org.dnltsk.yetanothercrutstransformer.model.Point
import org.junit.Test
import java.time.Instant

class GridParserTest {

    private val parser = GridParser()

    @Test
    fun gridRef_is_parsed_correctly() {
        val gridRef = parser.parseGridRef("Grid-ref= 116, 263")
        val expectedGridRef = GridRef(col = 116, row = 263)
        assertThat(gridRef).isEqualTo(expectedGridRef)
    }

    @Test
    fun gridRef_with_max_length_is_parsed_correctly() {
        val gridRef = parser.parseGridRef("Grid-ref=1111,9999")
        val expectedGridRef = GridRef(col = 1111, row = 9999)
        assertThat(gridRef).isEqualTo(expectedGridRef)
    }

    @Test
    fun year_line_is_parsed_correctly() {
        val pointOfYear = parser.parseYear(
                yearLine = " 2660 1003 1226 1689  775  598    5  181    8  321  261 1240",
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
    fun gridBox_is_parsed_correctly() {
        val gridBoxLines = listOf(
                "Grid-ref= 116, 266",
                "  405  405 1593  582  729  214  335  383    8  494 1501  807",
                "  498  361  392  691   98  558  271   68  253  691  684 1524",
                " 1321  442  995 1016  760  508  307  527    3  449  332 1028"
        )
        val pointsOfGridBox = parser.parseGridBox(
                gridBoxLines = gridBoxLines,
                years = listOf(2000, 1985, 2017)
        )
        val expectedFirstPoint = Point(
                date = Instant.parse("2000-01-01T00:00:00Z"),
                gridRef = GridRef(col = 116, row = 266),
                value = 405
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
        assertThat(pointsOfGridBox.size).isEqualTo(3 * 12)
        assertThat(pointsOfGridBox.first()).isEqualTo(expectedFirstPoint)
        assertThat(pointsOfGridBox.get(17)).isEqualTo(expectedMiddlePoint)
        assertThat(pointsOfGridBox.last()).isEqualTo(expectedLastPoint)
    }

    @Test
    fun gridBox_with_5_characters_long_values_are_parsed_correctly() {
        val gridBoxLines = listOf(
                "Grid-ref= 116, 266",
                "123451234512345123451234512345123451234512345123451234512345"
        )
        val pointsOfGridBox = parser.parseGridBox(
                gridBoxLines = gridBoxLines,
                years = listOf(2017)
        )

        val expectedValueOfAllGridPoints = 12345
        assertThat(pointsOfGridBox.size).isEqualTo(12)
        pointsOfGridBox.forEach { gridPoint ->
            assertThat(gridPoint.value).isEqualTo(expectedValueOfAllGridPoints)
        }
    }

    @Test
    fun sampleFile_is_parsed_correctly() {
        val allPoints = parser.parse(
                lines = GoldenTestData.sampleCruTsPreLines(),
                period = Period(fromYear = 1991, toYear = 2000)
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