package org.dnltsk.yetanothercrutstransformer.read

import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.dnltsk.yetanothercrutstransformer.GoldenTestData
import org.dnltsk.yetanothercrutstransformer.model.BBox
import org.dnltsk.yetanothercrutstransformer.model.GridSize
import org.dnltsk.yetanothercrutstransformer.model.Metadata
import org.dnltsk.yetanothercrutstransformer.model.Period
import org.junit.Test
import java.io.File

class MetadataParserTest {

    private val parser = MetadataParser(VersionValidator())

    @Test
    fun climaticVariable_is_parsed_correctly() {
        val climaticVariable = parser.parseClimateVariable(" foo Name (km/h)    ")
        assertThat(climaticVariable).isEqualTo("foo Name (km/h)")
    }

    @Test
    fun bbox_is_parsed_correctly() {
        val bbox = parser.parseBbox("[Long=-180.00, 180.00] [Lati= -90.00,  90.00] [Grid X,Y= 720, 360]")
        val expectedBbox = BBox(minX = -180f, minY = -90f, maxX = 180f, maxY = 90f)
        assertThat(bbox).isEqualTo(expectedBbox)
    }

    @Test
    fun size_is_parsed_correctly() {
        val size = parser.parseSize("[Long=-180.00, 180.00] [Lati= -90.00,  90.00] [Grid X,Y= 720, 360]")
        val expectedSize = GridSize(width = 720, height = 360)
        assertThat(size).isEqualTo(expectedSize)
    }

    @Test
    fun period_is_parsed_correctly() {
        val period = parser.parsePeriod("[Boxes=   67420] [Years=1991-2000] [Multi=    0.1000] [Missing=-999]")
        val expectedPeriod = Period(fromYear = 1991, toYear = 2000)
        assertThat(period).isEqualTo(expectedPeriod)
    }

    @Test
    fun metadata_of_sample_file_is_correct() {
        val metadata = parser.parse(File(GoldenTestData.sampleCruTsPrePath()).readLines())
        val expectedMetadata = Metadata(
                climaticVariable = ".pre = precipitation (mm)",
                bbox = BBox(minX = -180f, minY = -90f, maxX = 180f, maxY = 90f),
                gridSize = GridSize(width = 720, height = 360),
                period = Period(fromYear = 1991, toYear = 2000),
                multiplier = 0.1000f,
                missing = -999
        )
        assertThat(metadata).isEqualTo(expectedMetadata)
    }

}