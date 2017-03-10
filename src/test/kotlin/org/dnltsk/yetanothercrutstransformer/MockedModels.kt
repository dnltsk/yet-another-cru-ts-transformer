package org.dnltsk.yetanothercrutstransformer

import org.dnltsk.yetanothercrutstransformer.model.CruTs
import org.dnltsk.yetanothercrutstransformer.model.Metadata
import org.dnltsk.yetanothercrutstransformer.model.Point

class MockedModels {

    companion object {

        val mockedMetadata = Metadata(
                filename = "foo",
                cruTsVersion = "foo",
                weatherParameterName = "foo",
                bboxMinX = 0f,
                bboxMinY = 0f,
                bboxMaxX = 0f,
                bboxMaxY = 0f,
                numRows = 0,
                numCols = 0
        )

        val mockedPoints = mutableListOf<Point>()

        val mockedCruTs = CruTs(
                metadata = mockedMetadata,
                points = mockedPoints
        )

    }

}