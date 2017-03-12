package org.dnltsk.yetanothercrutstransformer.write

import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.dnltsk.yetanothercrutstransformer.MockedModels
import org.dnltsk.yetanothercrutstransformer.model.GridRef
import org.dnltsk.yetanothercrutstransformer.model.Point
import org.junit.Test
import java.time.Instant

class PointRepositoryTest : MemoryDbBase() {

    var pointRepository = PointRepository()
    var metadataRepository = MetadataRepository()

    @Test
    fun points_are_written_correctly() {

        val originalPoint = Point(
                date = Instant.parse("2000-12-01T00:00:00Z"),
                gridRef = GridRef(col = 142, row = 268),
                value = 272
        )
        metadataRepository.createMetadataTable(memoryConn)
        pointRepository.createPointTable(memoryConn)
        val newMetadataId = metadataRepository.insertMetadata(memoryConn, MockedModels.SAMPLE_METADATA)
        pointRepository.insertPoints(
                conn = memoryConn,
                points = listOf(originalPoint),
                metadataId = newMetadataId)

        val selectedPoint = pointRepository.selectPoint(
                conn = memoryConn,
                date = Instant.parse("2000-12-01T00:00:00Z"),
                gridRef = GridRef(col = 142, row = 268),
                metadataId = newMetadataId
        )
        assertThat(selectedPoint).isEqualTo(originalPoint)
    }
}