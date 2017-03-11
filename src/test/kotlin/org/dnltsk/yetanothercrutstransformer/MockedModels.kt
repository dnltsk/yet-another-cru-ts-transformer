package org.dnltsk.yetanothercrutstransformer

import org.dnltsk.yetanothercrutstransformer.model.*

class MockedModels {

    companion object {

        val mockedMetadata = Metadata(
                filename = "foo",
                cruTsVersion = "foo",
                weatherParameterName = "foo",
                bbox = BBox(0f, 0f, 0f, 0f),
                size = Size(0, 0)
        )

        val mockedPoints = mutableListOf<Point>()

        val mockedCruTs = CruTs(
                metadata = mockedMetadata,
                points = mockedPoints
        )

    }

}