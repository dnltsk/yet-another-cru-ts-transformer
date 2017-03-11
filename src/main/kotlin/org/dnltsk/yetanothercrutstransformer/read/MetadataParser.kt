package org.dnltsk.yetanothercrutstransformer.read

import com.google.inject.Singleton
import org.dnltsk.yetanothercrutstransformer.model.BBox
import org.dnltsk.yetanothercrutstransformer.model.Metadata
import org.dnltsk.yetanothercrutstransformer.model.Size
import org.slf4j.LoggerFactory
import java.io.File
import java.util.regex.Pattern

@Singleton
class MetadataParser() {

    private val LOG = LoggerFactory.getLogger(this::class.java)

    private val EXPECTED_VERSION = "CRU TS 2.1"

    private val WEATHER_PARAMETER_NAME_LINE_INDEX = 1
    private val VERSION_LINE_INDEX = 2
    private val BBOX_AND_SIZE_LINE_INDEX = 3

    fun parse(file: File): Metadata {
        val lines = file.readText().lines()
        return Metadata(
                filename = file.name,
                cruTsVersion = parseVersion(lines.get(VERSION_LINE_INDEX)),
                weatherParameterName = parseWeatherParameterName(lines.get(WEATHER_PARAMETER_NAME_LINE_INDEX)),
                bbox = parseBbox(lines.get(BBOX_AND_SIZE_LINE_INDEX)),
                size = parseSize(lines.get(BBOX_AND_SIZE_LINE_INDEX))
        )
    }

    fun parseVersion(line: String): String {
        val version = line.trim()
        LOG.info("version = $version")
        if (version != EXPECTED_VERSION) {
            LOG.warn("This parser expects version '$EXPECTED_VERSION' - results can be incorrect because you're using version '$version'")
        }
        return version
    }

    fun parseWeatherParameterName(line: String): String {
        val weatherParameter = line.trim()
        LOG.info("weatherParameterName = $weatherParameter")
        return weatherParameter
    }

    fun parseBbox(line: String): BBox {
        val bboxAndSize = parseBboxAndSizeLine(line)
        val bbox = BBox(
                minX = bboxAndSize.get(0).toFloat(),
                minY = bboxAndSize.get(2).toFloat(),
                maxX = bboxAndSize.get(1).toFloat(),
                maxY = bboxAndSize.get(3).toFloat()
        )
        LOG.info("bbox = $bbox")
        return bbox
    }

    fun parseSize(line: String): Size {
        val bboxAndSize = parseBboxAndSizeLine(line)
        val size = Size(
                cols = bboxAndSize.get(4).toInt(),
                rows = bboxAndSize.get(5).toInt()
        )
        LOG.info("size = $size")
        return size
    }

    fun parseBboxAndSizeLine(line: String): MutableList<String> {
        val line = line.trim()
        val matcher = Pattern.compile("([-\\d\\.]+)").matcher(line)
        val numbers = mutableListOf<String>()
        while (matcher.find()) {
            numbers.add(matcher.group())
        }
        return numbers
    }

}