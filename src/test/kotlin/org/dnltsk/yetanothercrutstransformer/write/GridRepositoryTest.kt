package org.dnltsk.yetanothercrutstransformer.write

import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.dnltsk.yetanothercrutstransformer.MockedModels
import org.dnltsk.yetanothercrutstransformer.model.GridPoint
import org.dnltsk.yetanothercrutstransformer.model.GridRef
import org.junit.Test
import java.time.Instant

class GridRepositoryTest : MemoryDbBase() {

    var gridRepository = GridRepository()
    var metadataRepository = MetadataRepository()

    @Test
    fun gridpoints_are_written_correctly() {

        val originalGridPoint = GridPoint(
                date = Instant.parse("2000-12-01T00:00:00Z"),
                gridRef = GridRef(col = 142, row = 268),
                value = 272
        )
        metadataRepository.createMetadataTableIfNotExists(memoryConn)
        gridRepository.createGridTableIfNotExists(memoryConn)
        val newMetadataId = metadataRepository.insertMetadata(memoryConn, MockedModels.SAMPLE_METADATA)
        gridRepository.insertGridPoints(
                conn = memoryConn,
                gridPoints = listOf(originalGridPoint),
                metadataId = newMetadataId)

        val selectedGridPoint = gridRepository.selectGridPoint(
                conn = memoryConn,
                date = Instant.parse("2000-12-01T00:00:00Z"),
                gridRef = GridRef(col = 142, row = 268),
                metadataId = newMetadataId
        )
        assertThat(selectedGridPoint).isEqualTo(originalGridPoint)
    }
}