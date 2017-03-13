package org.dnltsk.yetanothercrutstransformer

import org.dnltsk.yetanothercrutstransformer.model.*

class MockedModels {

    companion object {

        val SAMPLE_METADATA = Metadata(
                climaticVariable = "foo",
                bbox = BBox(-180f, -90f, 180f, 90f),
                gridSize = GridSize(10, 10),
                period = Period(fromYear = 2016, toYear = 2016),
                multiplier = 0.1f,
                missing = -999
        )

        val EMPTY_POINTS = mutableListOf<Point>()

        val SAMPLE_CRU_TS = CruTs(
                sourceFile = "foo.txt",
                metadata = SAMPLE_METADATA,
                points = EMPTY_POINTS
        )

    }

}