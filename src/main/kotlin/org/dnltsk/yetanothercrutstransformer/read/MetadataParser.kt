package org.dnltsk.yetanothercrutstransformer.read

import com.google.inject.Inject
import com.google.inject.Singleton
import org.dnltsk.yetanothercrutstransformer.model.BBox
import org.dnltsk.yetanothercrutstransformer.model.GridSize
import org.dnltsk.yetanothercrutstransformer.model.Metadata
import org.dnltsk.yetanothercrutstransformer.model.Period
import org.slf4j.LoggerFactory

@Singleton
class MetadataParser @Inject constructor() {

    private val LOG = LoggerFactory.getLogger(this::class.java)

    private val CLIMATIC_VARIABLE_LINE_INDEX = 1
    private val BBOX_AND_SIZE_LINE_INDEX = 3
    private val PERIOD_LINE_INDEX = 4
    private val MULTIPLIER_LINE_INDEX = 4
    private val MISSING_LINE_INDEX = 4

    fun parse(lines: List<String>): Metadata {
        return Metadata(
                climaticVariable = parseClimateVariable(lines.get(CLIMATIC_VARIABLE_LINE_INDEX)),
                bbox = parseBbox(lines.get(BBOX_AND_SIZE_LINE_INDEX)),
                gridSize = parseSize(lines.get(BBOX_AND_SIZE_LINE_INDEX)),
                period = parsePeriod(lines.get(PERIOD_LINE_INDEX)),
                multiplier = parseMultiplier(lines.get(MULTIPLIER_LINE_INDEX)),
                missing = parseMissing(lines.get(MISSING_LINE_INDEX))
        )
    }

    fun parseClimateVariable(line: String): String {
        val climateVariable = line.trim()
        LOG.info("climaticVariable = $climateVariable")
        return climateVariable
    }

    fun parseBbox(line: String): BBox {
        val bbox = BBox(
                minX = line.substring(6, 13).trim().toFloat(),
                minY = line.substring(29, 36).trim().toFloat(),
                maxX = line.substring(14, 21).trim().toFloat(),
                maxY = line.substring(37, 44).trim().toFloat()
        )
        LOG.info("bbox = $bbox")
        return bbox
    }

    fun parseSize(line: String): GridSize {
        val size = GridSize(
                width = line.substring(56, 60).trim().toInt(),
                height = line.substring(61, 65).trim().toInt()
        )
        LOG.info("gridSize = $size")
        return size
    }

    fun parsePeriod(line: String): Period {
        val period = Period(
                fromYear = line.substring(24, 28).trim().toInt(),
                toYear = line.substring(29, 33).trim().toInt())
        LOG.info("period = $period")
        return period
    }

    private fun parseMultiplier(line: String): Float {
        val multiplier = line.substring(42, 52).trim().toFloat()
        LOG.info("multiplier = $multiplier")
        return multiplier
    }

    private fun parseMissing(line: String): Int {
        val missing = line.substring(63, 67).trim().toInt()
        LOG.info("missing = $missing")
        return missing
    }


}