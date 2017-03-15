package org.dnltsk.yetanothercrutstransformer.read

import com.google.inject.Inject
import com.google.inject.Singleton
import org.dnltsk.yetanothercrutstransformer.model.CruTs
import java.io.File
import java.io.FileNotFoundException

@Singleton
class Parser @Inject constructor(
        val metadataParser: MetadataParser,
        val gridParser: GridParser
) {

    fun parse(filename: String?): CruTs {
        val file = openFile(filename)
        val lines = file.readText().lines()
        val metadata = metadataParser.parse(lines)
        val gridPoints = gridParser.parse(lines, metadata.period)
        return CruTs(
                sourceFile = file.absoluteFile.absolutePath,
                metadata = metadata,
                grid = gridPoints)
    }

    private fun openFile(filename: String?): File {
        val file = File(filename)
        if (!file.exists() || !file.canRead()) {
            throw FileNotFoundException("cannot access " + file.absoluteFile)
        }
        return file
    }


}