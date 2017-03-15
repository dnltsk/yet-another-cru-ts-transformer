package org.dnltsk.yetanothercrutstransformer.model

import java.time.Instant

data class GridPoint(
        val date: Instant,
        val gridRef: GridRef,
        val value: Int
)