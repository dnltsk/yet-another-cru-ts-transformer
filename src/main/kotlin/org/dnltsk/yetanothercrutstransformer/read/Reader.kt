package org.dnltsk.yetanothercrutstransformer.read

import com.google.inject.Inject
import com.google.inject.Singleton
import org.dnltsk.yetanothercrutstransformer.model.CruTs
import java.io.File
import java.io.FileNotFoundException

@Singleton
class Reader @Inject constructor(
        val metadataParser: MetadataParser,
        val pointReader: PointReader
) {

    fun read(filename: String?): CruTs {
        val file = openFile(filename)
        val metadata = metadataParser.parse(file)
        val points = pointReader.read(file)
        return CruTs(metadata = metadata, points = points)
    }

    private fun openFile(filename: String?): File {
        val file = File(filename)
        if (!file.exists() || !file.canRead()) {
            throw FileNotFoundException("cannot access " + file.absoluteFile)
        }
        return file
    }


}