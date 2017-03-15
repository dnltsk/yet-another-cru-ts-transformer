package org.dnltsk.yetanothercrutstransformer.write

import com.google.inject.Singleton
import org.dnltsk.yetanothercrutstransformer.model.GridPoint
import org.dnltsk.yetanothercrutstransformer.model.GridRef
import org.dnltsk.yetanothercrutstransformer.util.batch
import org.dnltsk.yetanothercrutstransformer.write.DbService.Companion.GRID_TABLE_NAME
import org.dnltsk.yetanothercrutstransformer.write.DbService.Companion.METADATA_TABLE_NAME
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement
import java.time.Instant

@Singleton
class GridRepository {

    private val LOG = LoggerFactory.getLogger(this::class.java)

    fun createGridTableIfNotExists(conn: Connection) {
        val sql = StringBuilder()
        sql.append("CREATE TABLE IF NOT EXISTS ${GRID_TABLE_NAME} ( ")
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

    fun insertGridPoints(conn: Connection, gridPoints: List<GridPoint>, metadataId: Int) {
        val start = System.currentTimeMillis()
        val batchSize = 500
        val numBatches = gridPoints.size / batchSize + 1
        val tenPercentSteps = calcTenPercentSteps(numBatches)
        gridPoints.asSequence().batch(batchSize).forEachIndexed { batchIndex, batchGridPoints ->
            var sql = StringBuilder()
            batchGridPoints.forEachIndexed { index, gridPoint ->
                if (index == 0) {
                    sql.append("INSERT INTO $GRID_TABLE_NAME ( ")
                    sql.append("    metadata_id, ")
                    sql.append("    xref, yref, ")
                    sql.append("    date, value ")
                    sql.append(" ) VALUES ")
                    sql.append(composeGridPointValues(metadataId, gridPoint))
                } else {
                    sql.append(" , ")
                    sql.append(composeGridPointValues(metadataId, gridPoint))
                }
            }
            //LOG.info(sql.toString() + " ... ")
            val stmt = conn.createStatement()
            stmt.executeUpdate(sql.toString())
            logProgress(batchIndex, batchSize, batchGridPoints.size, gridPoints.size, tenPercentSteps)
        }
        conn.commit()
        val durationInMillis = System.currentTimeMillis() - start
        LOG.info("insert duration = "+(durationInMillis/1000.0)+"s")
    }

    private fun composeGridPointValues(metadataId: Int, gridPoint: GridPoint): StringBuilder {
        val select = StringBuilder(" ( ")
        select.append("    ${metadataId}, ")
        select.append("    ${gridPoint.gridRef.col}, ")
        select.append("    ${gridPoint.gridRef.row}, ")
        select.append("    '${gridPoint.date}', ")
        select.append("    ${gridPoint.value} ")
        select.append(" ) ")
        return select
    }

    fun selectGridPoint(conn: Connection, date: Instant, gridRef: GridRef, metadataId: Int): GridPoint? {
        var prepStmt: Statement? = null
        var rs: ResultSet? = null
        try {
            val sql = StringBuilder()
            sql.append("SELECT * FROM $GRID_TABLE_NAME ")
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
                return GridPoint(
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

    private fun calcTenPercentSteps(numGridPoints: Int): IntProgression {
        var tenPercentStepDivision = 10
        if (numGridPoints < tenPercentStepDivision) {
            tenPercentStepDivision = numGridPoints
        }
        val step = numGridPoints / tenPercentStepDivision
        return IntProgression.fromClosedRange(1, numGridPoints, step)
    }

    private fun logProgress(batchIndex: Int, batchSize: Int, currentBatchSize: Int, numAllGridPoints: Int, steps: IntProgression) {
        val numInsertedGridPoints = batchIndex * batchSize + currentBatchSize
        if ((batchIndex != 0 && steps.contains(batchIndex)) || numInsertedGridPoints == numAllGridPoints) {
            val perc = Math.round((numInsertedGridPoints.toDouble() / numAllGridPoints.toDouble())*100.0)
            LOG.info("${numInsertedGridPoints} grid written ($perc%)...")
        }
    }

}