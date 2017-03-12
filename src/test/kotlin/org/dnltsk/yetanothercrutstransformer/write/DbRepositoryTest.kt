package org.dnltsk.yetanothercrutstransformer.write

import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.dnltsk.yetanothercrutstransformer.MockedModels
import org.dnltsk.yetanothercrutstransformer.model.GridRef
import org.dnltsk.yetanothercrutstransformer.model.Point
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.sql.Connection
import java.sql.DriverManager
import java.time.Instant

class DbRepositoryTest {

    var pointDbRepository = DbRepository()

    lateinit var memoryConn: Connection
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
    fun metadata_is_written_correctly() {
        val originalMetadata = MockedModels.SAMPLE_METADATA
        pointDbRepository.createMetadataTable(memoryConn)
        pointDbRepository.createPointTable(memoryConn)
        val newMetadataId = pointDbRepository.insertMetadata(memoryConn, originalMetadata)

        val selectedMetadata = pointDbRepository.selectMetadata(
                conn = memoryConn,
                metadataId = newMetadataId
        )
        assertThat(selectedMetadata).isEqualTo(originalMetadata)
    }

    @Test
    fun points_are_written_correctly() {

        val originalPoint = Point(
                date = Instant.parse("2000-12-01T00:00:00Z"),
                gridRef = GridRef(col = 142, row = 268),
                value = 272
        )
        pointDbRepository.createMetadataTable(memoryConn)
        pointDbRepository.createPointTable(memoryConn)
        val newMetadataId = pointDbRepository.insertMetadata(memoryConn, MockedModels.SAMPLE_METADATA)
        pointDbRepository.insertPoints(
                conn = memoryConn,
                points = listOf(originalPoint),
                metadataId = newMetadataId)

        val selectedPoint = pointDbRepository.selectPoint(
                conn = memoryConn,
                date = Instant.parse("2000-12-01T00:00:00Z"),
                gridRef = GridRef(col = 142, row = 268),
                metadataId = newMetadataId
        )
        assertThat(selectedPoint).isEqualTo(originalPoint)
    }
}