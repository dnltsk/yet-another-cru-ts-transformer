package org.dnltsk.yetanothercrutstransformer.model

data class CruTs(
        val sourceFile: String,
        val metadata: Metadata,
        val grid: List<GridPoint>
)

