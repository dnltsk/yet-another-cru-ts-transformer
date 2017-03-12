package org.dnltsk.yetanothercrutstransformer.write

import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.dnltsk.yetanothercrutstransformer.MockedModels
import org.junit.Test

class MetadataRepositoryTest : MemoryDbBase() {

    var metadataRepository = MetadataRepository()

    @Test
    fun metadata_is_written_correctly() {
        val originalMetadata = MockedModels.SAMPLE_METADATA
        metadataRepository.createMetadataTable(memoryConn)
        val newMetadataId = metadataRepository.insertMetadata(memoryConn, originalMetadata)

        val selectedMetadata = metadataRepository.selectMetadata(
                conn = memoryConn,
                metadataId = newMetadataId
        )
        assertThat(selectedMetadata).isEqualTo(originalMetadata)
    }

}