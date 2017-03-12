package org.dnltsk.yetanothercrutstransformer.write

import com.google.inject.Singleton
import org.dnltsk.yetanothercrutstransformer.model.GridRef
import org.dnltsk.yetanothercrutstransformer.model.Point
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement
import java.time.Instant

@Singleton
class GridDbRepository {

    private val LOG = LoggerFactory.getLogger(this::class.java)
    private val TARGET_TABLE = "CRU_TS_TABLE"

    fun createTable(conn: Connection) {
        val sql = StringBuilder()
        sql.append("CREATE TABLE $TARGET_TABLE ( ")
        sql.append("    xref INT, ")
        sql.append("    yref INT, ")
        sql.append("    date DATETIME, ")
        sql.append("    value INT,  ")
        sql.append("    PRIMARY KEY (xref, yref, date) ) ")
        LOG.info(sql.toString())

        val stmt = conn.createStatement()
        stmt.executeUpdate(sql.toString())
        stmt.close()
    }

    fun dropTableIfExists(conn: Connection) {
        val sql = "DROP TABLE IF EXISTS $TARGET_TABLE"
        LOG.info(sql)

        val stmt = conn.createStatement()
        stmt.executeUpdate(sql.toString())
        stmt.close()
    }

    fun insert(conn: Connection, points: List<Point>) {
        val sql = "INSERT INTO $TARGET_TABLE VALUES ( ?, ?, ?, ? ) "
        LOG.info(sql + " ... ")
        val tenPercentSteps = calcTenPercentSteps(points.size)
        points.forEachIndexed { index, point ->
            val prepStmt = conn.prepareStatement(sql)
            prepStmt.setInt(1, point.gridRef.col)
            prepStmt.setInt(2, point.gridRef.row)
            prepStmt.setString(3, point.date.toString())
            prepStmt.setInt(4, point.value)
            prepStmt.executeUpdate()
            prepStmt.close()
            logProgress(index, points.size, tenPercentSteps)
        }
        conn.commit()
        LOG.info("Done.")
        LOG.info("You can check the SQLite database via:")
        LOG.info("> sqlite3 -header -column ${SqLiteConnectionPool.TARGET_FILE} 'select * from CRU_TS_TABLE LIMIT 10'")
    }

    fun select(conn: Connection, date: Instant, gridRef: GridRef): Point? {
        var prepStmt: Statement? = null
        var rs: ResultSet? = null
        try {
            val sql = StringBuilder()
            sql.append("SELECT * FROM $TARGET_TABLE ")
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

    private fun  calcTenPercentSteps(numPoints: Int): IntProgression {
        return IntProgression.fromClosedRange(0, numPoints, numPoints/10)
    }

    private fun logProgress(index: Int, numPoints: Int, steps: IntProgression) {
        if (steps.contains(index)) {
            val perc = Math.round((index.toDouble() / numPoints.toDouble()) * 100.0)
            LOG.info("$index points written ($perc%)...")
        }
    }

}