package org.dnltsk.yetanothercrutstransformer.write

import com.google.inject.Singleton
import org.dnltsk.yetanothercrutstransformer.model.*
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement
import java.time.Instant

@Singleton
class DbRepository {

    companion object {
        val POINT_TABLE_NAME = "POINT_TABLE"
        val METADATA_TABLE_NAME = "METADATA_TABLE"
    }

    private val LOG = LoggerFactory.getLogger(this::class.java)

    fun createMetadataTable(conn: Connection) {
        val sql = StringBuilder()
        sql.append("CREATE TABLE IF NOT EXISTS $METADATA_TABLE_NAME ( ")
        sql.append("    id INTEGER PRIMARY KEY, ")
        sql.append("    imported_at DATETIME, ")
        sql.append("    climatic_variable VARCHAR(100), ")
        sql.append("    minX REAL, ")
        sql.append("    minY REAL, ")
        sql.append("    maxX REAL, ")
        sql.append("    maxY REAL, ")
        sql.append("    grid_width INTEGER, ")
        sql.append("    grid_height INTERGER, ")
        sql.append("    from_year INTERGER, ")
        sql.append("    to_year INTERGER, ")
        sql.append("    multiplier REAL, ")
        sql.append("    missing INTEGER  ")
        sql.append(" ) ")
        LOG.info(sql.toString())

        val stmt = conn.createStatement()
        stmt.executeUpdate(sql.toString())
        stmt.close()
    }

    fun createPointTable(conn: Connection) {
        val sql = StringBuilder()
        sql.append("CREATE TABLE IF NOT EXISTS $POINT_TABLE_NAME ( ")
        sql.append("    metadata_id INTEGER, ")
        sql.append("    xref INTEGER, ")
        sql.append("    yref INTEGER, ")
        sql.append("    date DATETIME, ")
        sql.append("    value INTEGER,  ")
        sql.append("    PRIMARY KEY (metadata_id, xref, yref, date), ")
        sql.append("    FOREIGN KEY(metadata_id) REFERENCES $METADATA_TABLE_NAME(id) ")
        sql.append(" ) ")
        LOG.info(sql.toString())

        val stmt = conn.createStatement()
        stmt.executeUpdate(sql.toString())
        stmt.close()
    }

    fun insertMetadata(conn: Connection, metadata: Metadata): Int {
        val sql = StringBuilder()
        sql.append("INSERT INTO $METADATA_TABLE_NAME ( ")
        sql.append("    imported_at, ")
        sql.append("    climatic_variable, ")
        sql.append("    minX, minY, maxX, maxY, ")
        sql.append("    grid_width, grid_height, ")
        sql.append("    from_year, to_year, ")
        sql.append("    multiplier, missing ")
        sql.append(" ) VALUES ( ")
        sql.append("    ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ")
        sql.append(" ) ")
        LOG.info(sql.toString())

        val prepStmt = conn.prepareStatement(sql.toString())
        prepStmt.setString(1, Instant.now().toString())
        prepStmt.setString(2, metadata.climaticVariable)
        prepStmt.setFloat(3, metadata.bbox.minX)
        prepStmt.setFloat(4, metadata.bbox.minY)
        prepStmt.setFloat(5, metadata.bbox.maxX)
        prepStmt.setFloat(6, metadata.bbox.maxY)
        prepStmt.setInt(7, metadata.gridSize.width)
        prepStmt.setInt(8, metadata.gridSize.height)
        prepStmt.setInt(9, metadata.period.fromYear)
        prepStmt.setInt(10, metadata.period.toYear)
        prepStmt.setFloat(11, metadata.multiplier)
        prepStmt.setInt(12, metadata.missing)

        prepStmt.executeUpdate()

        val newMetadataId = getGeneratedMetadataId(prepStmt)

        prepStmt.close()

        return newMetadataId
    }

    fun insertPoints(conn: Connection, points: List<Point>, metadataId: Int) {
        val sql = StringBuilder()
        sql.append("INSERT INTO $POINT_TABLE_NAME ( ")
        sql.append("    metadata_id, ")
        sql.append("    xref, yref, ")
        sql.append("    date, value ")
        sql.append(" ) VALUES ( ")
        sql.append("    ?, ?, ?, ?, ? ")
        sql.append(" ) ")
        LOG.info(sql.toString() + " ... ")
        val tenPercentSteps = calcTenPercentSteps(points.size)
        points.forEachIndexed { index, point ->
            val prepStmt = conn.prepareStatement(sql.toString())
            prepStmt.setInt(1, metadataId)
            prepStmt.setInt(2, point.gridRef.col)
            prepStmt.setInt(3, point.gridRef.row)
            prepStmt.setString(4, point.date.toString())
            prepStmt.setInt(5, point.value)
            prepStmt.executeUpdate()
            prepStmt.close()
            logProgress(index, points.size, tenPercentSteps)
        }
        conn.commit()
    }

    fun selectMetadata(conn: Connection, metadataId: Int): Metadata? {
        var prepStmt: Statement? = null
        var rs: ResultSet? = null
        try {
            val sql = StringBuilder()
            sql.append("SELECT * FROM $METADATA_TABLE_NAME ")
            sql.append("    WHERE id = ? ")
            LOG.info(sql.toString())
            prepStmt = conn.prepareStatement(sql.toString())
            prepStmt.setInt(1, metadataId)

            rs = prepStmt.executeQuery()
            if (rs.next()) {
                return Metadata(
                        climaticVariable = rs.getString("climatic_variable"),
                        bbox = BBox(
                                minX = rs.getFloat("minX"),
                                minY = rs.getFloat("minY"),
                                maxX = rs.getFloat("maxX"),
                                maxY = rs.getFloat("maxY")
                        ),
                        gridSize = GridSize(
                                width = rs.getInt("grid_width"),
                                height = rs.getInt("grid_height")
                        ),
                        period = Period(
                                fromYear = rs.getInt("from_year"),
                                toYear = rs.getInt("to_year")
                        ),
                        multiplier = rs.getFloat("multiplier"),
                        missing = rs.getInt("missing")
                )
            }
        } finally {
            rs?.close()
            prepStmt?.close()
        }
        return null
    }

    fun selectPoint(conn: Connection, date: Instant, gridRef: GridRef, metadataId: Int): Point? {
        var prepStmt: Statement? = null
        var rs: ResultSet? = null
        try {
            val sql = StringBuilder()
            sql.append("SELECT * FROM $POINT_TABLE_NAME ")
            sql.append("    WHERE xref = ? AND yref = ? AND date = ? ")
            LOG.info(sql.toString() + " ... ")
            prepStmt = conn.prepareStatement(sql.toString())
            prepStmt.setInt(1, gridRef.col)
            prepStmt.setInt(2, gridRef.row)
            prepStmt.setString(3, date.toString())

            rs = prepStmt.executeQuery()
            if (rs.next()) {
                return Point(
                        gridRef = GridRef(col = rs.getInt("xref"), row = rs.getInt("yref")),
                        date = Instant.parse(rs.getString("date")),
                        value = rs.getInt("value")
                )
            }
        } finally {
            rs?.close()
            prepStmt?.close()
        }
        return null
    }

    private fun getGeneratedMetadataId(prepStmt: PreparedStatement): Int {
        val rs = prepStmt.generatedKeys
        rs.next()
        val newMetadataId = rs.getInt(1)
        rs.close()
        return newMetadataId
    }

    private fun calcTenPercentSteps(numPoints: Int): IntProgression {
        var tenPercentStepDivision = 10
        if (numPoints < tenPercentStepDivision) {
            tenPercentStepDivision = numPoints
        }
        val step = numPoints / tenPercentStepDivision
        return IntProgression.fromClosedRange(0, numPoints, step)
    }

    private fun logProgress(index: Int, numPoints: Int, steps: IntProgression) {
        if (steps.contains(index)) {
            val perc = Math.round((index.toDouble() / numPoints.toDouble()) * 100.0)
            LOG.info("$index points written ($perc%)...")
        }
    }

}