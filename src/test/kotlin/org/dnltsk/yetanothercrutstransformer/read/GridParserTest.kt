package org.dnltsk.yetanothercrutstransformer.read

import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.dnltsk.yetanothercrutstransformer.GoldenTestData
import org.dnltsk.yetanothercrutstransformer.model.GridPoint
import org.dnltsk.yetanothercrutstransformer.model.GridRef
import org.dnltsk.yetanothercrutstransformer.model.Period
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
        val gridPointsOfYear = parser.parseYearLine(
                yearLine = " 2660 1003 1226 1689  775  598    5  181    8  321  261 1240",
                year = 2017,
                gridRef = GridRef(col = 116, row = 263))
        val expectedJanuaryGridPoint = GridPoint(
                date = Instant.parse("2017-01-01T00:00:00Z"),
                gridRef = GridRef(col = 116, row = 263),
                value = 2660
        )
        val expectedJulyGridPoint = GridPoint(
                date = Instant.parse("2017-07-01T00:00:00Z"),
                gridRef = GridRef(col = 116, row = 263),
                value = 5
        )
        val expectedDecemberGridPoint = GridPoint(
                date = Instant.parse("2017-12-01T00:00:00Z"),
                gridRef = GridRef(col = 116, row = 263),
                value = 1240
        )
        assertThat(gridPointsOfYear.first()).isEqualTo(expectedJanuaryGridPoint)
        assertThat(gridPointsOfYear.get(6)).isEqualTo(expectedJulyGridPoint)
        assertThat(gridPointsOfYear.last()).isEqualTo(expectedDecemberGridPoint)
    }

    @Test
    fun gridBox_is_parsed_correctly() {
        val gridBoxLines = listOf(
                "Grid-ref= 116, 266",
                "  405  405 1593  582  729  214  335  383    8  494 1501  807",
                "  498  361  392  691   98  558  271   68  253  691  684 1524",
                " 1321  442  995 1016  760  508  307  527    3  449  332 1028"
        )
        val gridPointsOfGridBox = parser.parseGridBox(
                gridBoxLines = gridBoxLines,
                years = listOf(2000, 1985, 2017)
        )
        val expectedFirstGridPoint = GridPoint(
                date = Instant.parse("2000-01-01T00:00:00Z"),
                gridRef = GridRef(col = 116, row = 266),
                value = 405
        )
        val expectedMiddleGridPoint = GridPoint(
                date = Instant.parse("1985-06-01T00:00:00Z"),
                gridRef = GridRef(col = 116, row = 266),
                value = 558
        )
        val expectedLastGridPoint = GridPoint(
                date = Instant.parse("2017-12-01T00:00:00Z"),
                gridRef = GridRef(col = 116, row = 266),
                value = 1028
        )
        assertThat(gridPointsOfGridBox.size).isEqualTo(3 * 12)
        assertThat(gridPointsOfGridBox.first()).isEqualTo(expectedFirstGridPoint)
        assertThat(gridPointsOfGridBox.get(17)).isEqualTo(expectedMiddleGridPoint)
        assertThat(gridPointsOfGridBox.last()).isEqualTo(expectedLastGridPoint)
    }

    @Test
    fun gridBox_with_5_characters_long_values_are_parsed_correctly() {
        val gridBoxLines = listOf(
                "Grid-ref= 116, 266",
                "123451234512345123451234512345123451234512345123451234512345"
        )
        val gridPointsOfGridBox = parser.parseGridBox(
                gridBoxLines = gridBoxLines,
                years = listOf(2017)
        )

        val expectedValueOfAllGridPoints = 12345
        assertThat(gridPointsOfGridBox.size).isEqualTo(12)
        gridPointsOfGridBox.forEach { gridPoint ->
            assertThat(gridPoint.value).isEqualTo(expectedValueOfAllGridPoints)
        }
    }

    @Test
    fun sampleFile_is_parsed_correctly() {
        val allGridPoints = parser.parse(
                lines = GoldenTestData.sampleCruTsPreLines(),
                period = Period(fromYear = 1991, toYear = 2000)
        )
        val expectedFirstGridPoint = GridPoint(
                date = Instant.parse("1991-01-01T00:00:00Z"),
                gridRef = GridRef(col = 1, row = 148),
                value = 3020
        )
        val expectedLastGridPoint = GridPoint(
                date = Instant.parse("2000-12-01T00:00:00Z"),
                gridRef = GridRef(col = 142, row = 268),
                value = 272
        )
        assertThat(allGridPoints.first()).isEqualTo(expectedFirstGridPoint)
        assertThat(allGridPoints.last()).isEqualTo(expectedLastGridPoint)
    }
}