package org.dnltsk.yetanothercrutstransformer.read

import com.google.inject.Inject
import com.google.inject.Singleton
import org.dnltsk.yetanothercrutstransformer.model.CruTs
import java.io.File
import java.io.FileNotFoundException

@Singleton
class Reader @Inject constructor(
        val metadataParser: MetadataParser,
        val pointParser: PointParser
) {

    fun read(filename: String?): CruTs {
        val file = openFile(filename)
        val lines = file.readText().lines()
        val metadata = metadataParser.parse(lines)
        val points = pointParser.read(lines)
        return CruTs(
                sourceFile = file.absoluteFile.absolutePath,
                metadata = metadata,
                points = points)
    }

    private fun openFile(filename: String?): File {
        val file = File(filename)
        if (!file.exists() || !file.canRead()) {
            throw FileNotFoundException("cannot access " + file.absoluteFile)
        }
        return file
    }


}