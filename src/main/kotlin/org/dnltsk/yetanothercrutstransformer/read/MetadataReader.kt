package org.dnltsk.yetanothercrutstransformer.read

import com.google.inject.Singleton
import org.dnltsk.yetanothercrutstransformer.model.Metadata
import java.io.File

@Singleton
class MetadataReader() {

    fun read(file: File): Metadata {
        throw NotImplementedError()
    }

}