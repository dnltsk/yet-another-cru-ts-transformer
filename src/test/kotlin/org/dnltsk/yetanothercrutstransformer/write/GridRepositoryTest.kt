package org.dnltsk.yetanothercrutstransformer.write

import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.dnltsk.yetanothercrutstransformer.model.GridRef
import org.dnltsk.yetanothercrutstransformer.model.Point
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.sql.Connection
import java.sql.DriverManager
import java.time.Instant

class GridRepositoryTest {

    var pointDbRepository = GridDbRepository()

    lateinit var memoryConn : Connection
    @Before
    fun setUp() {
        memoryConn = DriverManager.getConnection("jdbc:sqlite::memory:")
        memoryConn.autoCommit = false
    }

    @After
    fun tearDown() {
        memoryConn.close()
    }

    @Test
    fun data_is_written_correctly() {

        val newPoint = Point(
                date = Instant.parse("2000-12-01T00:00:00Z"),
                gridRef = GridRef(col = 142, row = 268),
                value = 272
        )

        pointDbRepository.createTable(memoryConn)
        pointDbRepository.insert(memoryConn, listOf(newPoint))
        val selectedPoint = pointDbRepository.select(
                conn = memoryConn,
                date = Instant.parse("2000-12-01T00:00:00Z"),
                gridRef = GridRef(col = 142, row = 268)
        )
        assertThat(newPoint).isEqualTo(selectedPoint)
    }
}