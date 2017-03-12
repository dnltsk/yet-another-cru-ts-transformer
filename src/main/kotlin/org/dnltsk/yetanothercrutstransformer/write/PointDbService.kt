package org.dnltsk.yetanothercrutstransformer.write

import com.google.inject.Inject
import com.google.inject.Singleton
import org.dnltsk.yetanothercrutstransformer.model.Point
import org.slf4j.LoggerFactory
import java.sql.Connection

@Singleton
class PointDbService @Inject constructor(
        private val pointDbRepository: PointDbRepository,
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

    fun persist(points: List<Point>){
        var conn: Connection? = null
        try {
            conn = sqLiteConnectionPool.getConnection()
            conn.autoCommit = false
            pointDbRepository.dropTableIfExists(conn)
            pointDbRepository.createTable(conn)
            pointDbRepository.insert(conn, points)
            conn.commit()
        }finally {
            conn?.close()
        }
    }

}