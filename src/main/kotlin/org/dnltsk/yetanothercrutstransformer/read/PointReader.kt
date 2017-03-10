package org.dnltsk.yetanothercrutstransformer.read

import com.google.inject.Singleton
import org.dnltsk.yetanothercrutstransformer.model.Point
import java.io.File

@Singleton
class PointReader() {

    fun read(file: File): List<Point> {
        throw NotImplementedError()
    }

}