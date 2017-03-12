package org.dnltsk.yetanothercrutstransformer

import org.dnltsk.yetanothercrutstransformer.model.*

class MockedModels {

    companion object {

        val mockedMetadata = Metadata(
                climaticVariable = "foo",
                bbox = BBox(0f, 0f, 0f, 0f),
                gridSize = GridSize(0, 0),
                period = Period(fromYear = 0, toYear = 0),
                multiplier = 0f,
                missing = 0
        )

        val mockedPoints = mutableListOf<Point>()

        val mockedCruTs = CruTs(
                sourceFile = "/foo/bar.txt",
                metadata = mockedMetadata,
                points = mockedPoints
        )

    }

}