package org.dnltsk.yetanothercrutstransformer.model

data class Metadata(
    val filename: String,
    val cruTsVersion: String,
    val weatherParameterName: String,
    val bboxMinX: Float,
    val bboxMinY: Float,
    val bboxMaxX: Float,
    val bboxMaxY: Float,
    val numRows: Int,
    val numCols: Int){
}