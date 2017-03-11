package org.dnltsk.yetanothercrutstransformer.read

import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.dnltsk.yetanothercrutstransformer.GoldenTestData
import org.dnltsk.yetanothercrutstransformer.model.BBox
import org.dnltsk.yetanothercrutstransformer.model.Metadata
import org.dnltsk.yetanothercrutstransformer.model.Size
import org.junit.Test
import java.io.File

class MetadataParserTest {

    private val parser = MetadataParser()

    @Test
    fun version_is_parsed_correctly() {
        val version = parser.parseVersion(" foo Version   ")
        assertThat(version).isEqualTo("foo Version")
    }

    @Test
    fun weatherParameterName_is_parsed_correctly() {
        val weatherParameterName = parser.parseWeatherParameterName(" foo Name (km/h)    ")
        assertThat(weatherParameterName).isEqualTo("foo Name (km/h)")
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
        val expectedSize = Size(cols = 720, rows = 360)
        assertThat(size).isEqualTo(expectedSize)
    }

    @Test
    fun years_are_parsed_correctly() {
        val years = parser.parseYears("[Boxes=   67420] [Years=1991-2000] [Multi=    0.1000] [Missing=-999]")
        val expectedYears = (1991..2000).toList()
        assertThat(years).isEqualTo(expectedYears)
    }

    @Test
    fun metadata_of_sample_file_is_correct() {
        val metadata = parser.parse(File(GoldenTestData.sampleCruTsPreFile()).readLines())
        val expectedMetadata = Metadata(
                cruTsVersion = "CRU TS 2.1",
                weatherParameterName = ".pre = precipitation (mm)",
                bbox = BBox(minX = -180f, minY = -90f, maxX = 180f, maxY = 90f),
                size = Size(cols = 720, rows = 360),
                years = (1991..2000).toList()
        )
        assertThat(metadata).isEqualTo(expectedMetadata)
    }

}