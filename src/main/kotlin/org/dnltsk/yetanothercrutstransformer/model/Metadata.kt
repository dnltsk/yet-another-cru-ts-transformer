package org.dnltsk.yetanothercrutstransformer.model

data class Metadata(
        val filename: String,
        val cruTsVersion: String,
        val weatherParameterName: String,
        val bbox: BBox,
        val size: Size){
}