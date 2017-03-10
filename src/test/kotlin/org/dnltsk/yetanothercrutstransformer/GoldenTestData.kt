package org.dnltsk.yetanothercrutstransformer

import java.nio.file.Paths

class GoldenTestData {

    companion object {

        fun pathToSampleCruTsFilePre(): String {
            val path = Paths.get(this::class.java.getResource("/golden-test-data/sample-cru-ts-file.pre").toURI())
            return path.toString()
        }
    }

}