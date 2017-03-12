package org.dnltsk.yetanothercrutstransformer.model

data class Metadata(
        val climaticVariable: String,
        val bbox: BBox,
        val gridSize: GridSize,
        val period: Period,
        val multiplier: Float,
        val missing: Int
        )
