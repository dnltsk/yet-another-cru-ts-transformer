package org.dnltsk.yetanothercrutstransformer

import java.io.File
import java.nio.file.Paths

class GoldenTestData {

    companion object {

        fun sampleCruTsPreFile(): String {
            val path = Paths.get(this::class.java.getResource("/golden-test-data/sample-cru-ts-file.pre").toURI())
            return path.toString()
        }

        fun sampleCruTsPreLines():List<String>{
            return File(sampleCruTsPreFile()).readLines()
        }
    }

}