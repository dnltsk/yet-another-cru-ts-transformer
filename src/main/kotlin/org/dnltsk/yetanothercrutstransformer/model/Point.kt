package org.dnltsk.yetanothercrutstransformer.model

import java.time.Instant

data class Point(
        val date: Instant,
        val gridRef: GridRef,
        val value: Int
)