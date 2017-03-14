package org.dnltsk.yetanothercrutstransformer.write

import com.google.inject.Singleton
import org.dnltsk.yetanothercrutstransformer.model.GridRef
import org.dnltsk.yetanothercrutstransformer.model.Point
import org.dnltsk.yetanothercrutstransformer.util.batch
import org.dnltsk.yetanothercrutstransformer.write.DbService.Companion.METADATA_TABLE_NAME
import org.dnltsk.yetanothercrutstransformer.write.DbService.Companion.POINT_TABLE_NAME
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement
import java.time.Instant

@Singleton
class PointRepository {

    private val LOG = LoggerFactory.getLogger(this::class.java)

    fun createPointTable(conn: Connection) {
        val sql = StringBuilder()
        sql.append("CREATE TABLE IF NOT EXISTS ${POINT_TABLE_NAME} ( ")
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

    fun insertPoints(conn: Connection, points: List<Point>, metadataId: Int) {
        val start = System.currentTimeMillis()
        val batchSize = 1
        val numBatches = points.size / batchSize
        val tenPercentSteps = calcTenPercentSteps(numBatches)
        points.asSequence().batch(batchSize).forEachIndexed { batchIndex, batchPoints ->
            var sql = StringBuilder()
            batchPoints.forEachIndexed { index, point ->
                if (index == 0) {
                    sql.append("INSERT INTO $POINT_TABLE_NAME ( ")
                    sql.append("    metadata_id, ")
                    sql.append("    xref, yref, ")
                    sql.append("    date, value ")
                    sql.append(" ) VALUES ")
                    sql.append(composePointAsSelect(metadataId, point))
                } else {
                    sql.append(" , ")
                    sql.append(composePointAsSelect(metadataId, point))
                }
            }
            //LOG.info(sql.toString() + " ... ")
            val stmt = conn.createStatement()
            stmt.executeUpdate(sql.toString())
            logProgress(batchIndex, batchSize, batchPoints.size, points.size, tenPercentSteps)
        }
        conn.commit()
        val durationInMillis = System.currentTimeMillis() - start
        LOG.info("insert duration = "+(durationInMillis/1000.0)+"s")
    }

    private fun composePointAsSelect(metadataId: Int, point: Point): StringBuilder {
        val select = StringBuilder(" ( ")
        select.append("    ${metadataId}, ")
        select.append("    ${point.gridRef.col}, ")
        select.append("    ${point.gridRef.row}, ")
        select.append("    '${point.date}', ")
        select.append("    ${point.value} ")
        select.append(" ) ")
        return select
    }

    fun selectPoint(conn: Connection, date: Instant, gridRef: GridRef, metadataId: Int): Point? {
        var prepStmt: Statement? = null
        var rs: ResultSet? = null
        try {
            val sql = StringBuilder()
            sql.append("SELECT * FROM $POINT_TABLE_NAME ")
            sql.append(" WHERE ")
            sql.append("    metadata_id = ? ")
            sql.append("    AND xref = ? ")
            sql.append("    AND yref = ? ")
            sql.append("    AND date = ?")
            LOG.info(sql.toString() + " ... ")
            prepStmt = conn.prepareStatement(sql.toString())
            prepStmt.setInt(1, metadataId)
            prepStmt.setInt(2, gridRef.col)
            prepStmt.setInt(3, gridRef.row)
            prepStmt.setString(4, date.toString())

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

    private fun calcTenPercentSteps(numPoints: Int): IntProgression {
        var tenPercentStepDivision = 10
        if (numPoints < tenPercentStepDivision) {
            tenPercentStepDivision = numPoints
        }
        val step = numPoints / tenPercentStepDivision
        return IntProgression.fromClosedRange(1, numPoints, step)
    }

    private fun logProgress(batchIndex: Int, batchSize: Int, currentBatchSize: Int, numAllPoints: Int, steps: IntProgression) {
        val numInsertedPoints = batchIndex * batchSize + currentBatchSize
        if ((batchIndex != 0 && steps.contains(batchIndex)) || numInsertedPoints == numAllPoints) {
            val perc = Math.round((numInsertedPoints.toDouble() / numAllPoints.toDouble())*100.0)
            LOG.info("${numInsertedPoints} points written ($perc%)...")
        }
    }

}