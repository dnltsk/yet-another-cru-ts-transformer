package org.dnltsk.yetanothercrutstransformer.write.geotiff

import com.google.inject.Inject
import com.google.inject.Singleton
import com.sun.media.imageio.plugins.tiff.BaselineTIFFTagSet
import org.dnltsk.yetanothercrutstransformer.model.CruTs
import org.dnltsk.yetanothercrutstransformer.model.GridRef
import org.dnltsk.yetanothercrutstransformer.model.Metadata
import org.dnltsk.yetanothercrutstransformer.model.Point
import org.geotools.coverage.CoverageFactoryFinder
import org.geotools.coverage.grid.GridCoverage2D
import org.geotools.coverage.grid.io.AbstractGridFormat
import org.geotools.coverage.grid.io.imageio.geotiff.CRS2GeoTiffMetadataAdapter
import org.geotools.gce.geotiff.GeoTiffFormat
import org.geotools.gce.geotiff.GeoTiffWriteParams
import org.geotools.gce.geotiff.GeoTiffWriter
import org.geotools.geometry.Envelope2D
import org.geotools.referencing.crs.DefaultGeographicCRS
import java.awt.image.DataBuffer
import java.awt.image.WritableRaster
import java.io.File
import java.time.Instant
import javax.media.jai.RasterFactory


@Singleton
class GeotiffWriter @Inject constructor() {

    fun writeGeotiff(cruTs: CruTs) {

        val gridCoverage = createGridCoverage(cruTs.metadata, cruTs.points)

        val writer = GeoTiffWriter(File("the_geotiff.tif"))

        val wp = GeoTiffWriteParams()
        wp.compressionMode = GeoTiffWriteParams.MODE_EXPLICIT
        wp.compressionType = "LZW"
        val params = GeoTiffFormat().writeParameters
        params.parameter(AbstractGridFormat.GEOTOOLS_WRITE_PARAMS.name.toString()).setValue(wp)

        writer.setMetadataValue("foo", "bar")
        writer.setMetadataValue("band 1", "2017-10-10T00:00:00Z")
        writer.setMetadataValue("band 2", "2017-10-10T00:00:00Z")
        writer.setMetadataValue("band 3", "2017-10-10T00:00:00Z")
        writer.setMetadataValue("missing", "${cruTs.metadata.missing}")
        writer.setMetadataValue("multiplier", "${cruTs.metadata.multiplier}")
        writer.setMetadataValue(BaselineTIFFTagSet.TAG_IMAGE_DESCRIPTION.toString(), "foo description")
        writer.setMetadataValue(BaselineTIFFTagSet.TAG_IMAGE_WIDTH.toString(), cruTs.metadata.gridSize.width.toString())
        writer.setMetadataValue(BaselineTIFFTagSet.TAG_IMAGE_LENGTH.toString(), cruTs.metadata.gridSize.height.toString())
        writer.write(gridCoverage, params.values().toTypedArray())
    }

    private fun createGridCoverage(metadata: Metadata, points: List<Point>): GridCoverage2D {

        /*
         * Set the pixel values.  Because we use only one tile with one band, the code below
         * is pretty similar to the code we would have if we were just setting the values in
         * a matrix.
         */
        val width = metadata.gridSize.width
        val height = metadata.gridSize.height
        val yearList = (metadata.period.fromYear..metadata.period.toYear).toList()
        val numYears = yearList.size
        val numMonth = 12
        val bands = numYears * numMonth
        val raster: WritableRaster = RasterFactory.createBandedRaster(DataBuffer.TYPE_INT, width, height, bands, null)

        val groupedPoints = points.groupBy { it.gridRef }

        for (yearIndex in 0..numYears - 1) {
            for (monthIndex in 0..numMonth - 1) {
                val year = yearList.get(yearIndex)
                val month = (monthIndex + 1).toString().padStart(2, '0')
                val date = Instant.parse("$year-$month-01T00:00:00Z")
                val bandIndex = monthYearToBandIndex(monthIndex, yearIndex)
                for (y in 0..height - 1) {
                    for (x in 0..width - 1) {
                        val value = getValue(x, y, metadata.gridSize.height, date, groupedPoints, metadata.missing)
                        raster.setSample(x, y, bandIndex, value)
                    }
                }
            }
        }
        /*
         * Set some metadata (the CRS, the geographic envelope, etc.) and display the image.
         * The display may be slow, since the translation from floating-point values to some
         * color (or grayscale) is performed on the fly everytime the image is rendered.
         */
        val crs = DefaultGeographicCRS.WGS84

        // creating geotiff metadata
        val adapter = CRS2GeoTiffMetadataAdapter(crs)
        val gtMetadata = adapter.parseCoordinateReferenceSystem()
        gtMetadata.noData = metadata.missing.toDouble()

        val envelope = Envelope2D(
                crs,
                metadata.bbox.minX.toDouble(),
                metadata.bbox.minY.toDouble(),
                metadata.bbox.maxX.toDouble() - metadata.bbox.minX.toDouble(),
                metadata.bbox.maxY.toDouble() - metadata.bbox.minY.toDouble())
        val factory = CoverageFactoryFinder.getGridCoverageFactory(null)
        val gridCoverage = factory.create("My grayscale coverage", raster, envelope)

        return gridCoverage
    }

    private fun monthYearToBandIndex(monthIndex: Int, yearIndex: Int): Int {
        return yearIndex * 12 + monthIndex
    }

    private fun getValue(x: Int, y: Int, height: Int, date: Instant, points: Map<GridRef, List<Point>>, missing: Int): Int {
        val pointList = points.get(GridRef(col = x, row = height - y))
        if (pointList == null)
            return missing

        val found = pointList.find { it.date == date }
        return found?.value ?: missing
    }

}