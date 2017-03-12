package org.dnltsk.yetanothercrutstransformer.write

import com.google.inject.Inject
import com.google.inject.Singleton
import org.dnltsk.yetanothercrutstransformer.model.CruTs
import org.slf4j.LoggerFactory
import java.sql.Connection

@Singleton
class DbService @Inject constructor(
        private val dbRepository: DbRepository,
        private val sqLiteConnectionPool: SqLiteConnectionPool
){

    private val LOG = LoggerFactory.getLogger(this::class.java)

    init {
        try {
            Class.forName("org.sqlite.JDBC")
        } catch (all: Throwable) {
            LOG.error("Failed to load the org.sqlite.JDBC driver")
        }
    }

    fun persist(cruTs: CruTs){
        var conn: Connection? = null
        try {
            conn = sqLiteConnectionPool.getConnection()
            conn.autoCommit = false
            dbRepository.createMetadataTable(conn)
            dbRepository.createPointTable(conn)
            val newMetadataId = dbRepository.insertMetadata(conn, cruTs.metadata)
            dbRepository.insertPoints(conn, cruTs.points, newMetadataId)
            conn.commit()
            printSuccessMessage(newMetadataId)
        }finally {
            conn?.close()
        }
    }

    private fun printSuccessMessage(newMetadataId: Int) {
        LOG.info("Done.")
        LOG.info("You can check the SQLite database via:")
        LOG.info("> sqlite3 -header -column ${SqLiteConnectionPool.TARGET_FILE} 'SELECT * FROM ${DbRepository.METADATA_TABLE_NAME} WHERE id = $newMetadataId'")
        LOG.info("> sqlite3 -header -column ${SqLiteConnectionPool.TARGET_FILE} 'SELECT * FROM ${DbRepository.POINT_TABLE_NAME} WHERE metadata_id = $newMetadataId LIMIT 10'")
    }

}