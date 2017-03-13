package org.dnltsk.yetanothercrutstransformer.write.geotiff

import org.dnltsk.yetanothercrutstransformer.MockedModels
import org.junit.Test

class GeotiffWriterTest {

    @Test
    fun createGeotiff() {
        GeotiffWriter().writeGeotiff(MockedModels.SAMPLE_CRU_TS)
    }
}