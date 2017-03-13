package org.dnltsk.yetanothercrutstransformer.write

import com.google.inject.Inject
import com.google.inject.Singleton
import org.dnltsk.yetanothercrutstransformer.model.CruTs
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.DriverManager

@Singleton
class DbService @Inject constructor(
        private val pointRepository: PointRepository,
        private val metadataRepository: MetadataRepository
){

    private val LOG = LoggerFactory.getLogger(this::class.java)

    companion object {
        val TARGET_FILE = "cru-ts.sqlite"
        val POINT_TABLE_NAME = "POINT_TABLE"
        val METADATA_TABLE_NAME = "METADATA_TABLE"
    }

    fun persist(cruTs: CruTs){
        var conn: Connection? = null
        try {
            conn = openConnection()
            conn.autoCommit = false
            metadataRepository.createMetadataTable(conn)
            pointRepository.createPointTable(conn)
            val newMetadataId = metadataRepository.insertMetadata(conn, cruTs.metadata)
            pointRepository.insertPoints(conn, cruTs.points, newMetadataId)
            conn.commit()
            printSuccessMessage(newMetadataId)
        }finally {
            conn?.close()
        }
    }

    private fun openConnection(): Connection {
        return DriverManager.getConnection("jdbc:sqlite:$TARGET_FILE")
    }

    private fun printSuccessMessage(newMetadataId: Int) {
        LOG.info("Done.")
        LOG.info("You can check the SQLite database via:")
        LOG.info("> sqlite3 -header -column ${TARGET_FILE} 'SELECT * FROM ${METADATA_TABLE_NAME} WHERE id = $newMetadataId'")
        LOG.info("> sqlite3 -header -column ${TARGET_FILE} 'SELECT * FROM ${POINT_TABLE_NAME} WHERE metadata_id = $newMetadataId LIMIT 10'")
    }

}