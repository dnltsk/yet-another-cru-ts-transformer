package org.dnltsk.yetanothercrutstransformer.write

import com.google.inject.Singleton
import org.dnltsk.yetanothercrutstransformer.model.BBox
import org.dnltsk.yetanothercrutstransformer.model.GridSize
import org.dnltsk.yetanothercrutstransformer.model.Metadata
import org.dnltsk.yetanothercrutstransformer.model.Period
import org.dnltsk.yetanothercrutstransformer.write.DbService.Companion.METADATA_TABLE_NAME
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement
import java.time.Instant

@Singleton
class MetadataRepository {

    private val LOG = LoggerFactory.getLogger(this::class.java)

    fun createMetadataTableIfNotExists(conn: Connection) {
        val sql = StringBuilder()
        sql.append("CREATE TABLE IF NOT EXISTS ${METADATA_TABLE_NAME} ( ")
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

    private fun getGeneratedMetadataId(prepStmt: PreparedStatement): Int {
        val rs = prepStmt.generatedKeys
        rs.next()
        val newMetadataId = rs.getInt(1)
        rs.close()
        return newMetadataId
    }

}