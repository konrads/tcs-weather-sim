package com.tcs.weathersim.util

import java.awt.image.DataBufferByte
import javax.imageio.ImageIO

import com.tcs.weathersim.model.canonical._

/**
  * PNGGrid encapsulates a PNG image that represents a map in Latitude/Longitude coordinates.
  */
class PNGGrid[T](width: Int, height: Int, data: Array[Byte],
                 minLong: Longitude = Longitude(-180), maxLong: Longitude = Longitude(180),
                 minLat: Latitude = Latitude(-90), maxLat: Latitude = Latitude(90),
                 normalizer: (Int) => T) {
  assert(width > 0, "width <= 0")
  assert(height > 0, "height <= 0")
  assert(maxLong.value > minLong.value, "maxLong <= minLong")
  assert(maxLat.value > minLat.value, "maxLat <= minLat")
  private val longRange = maxLong.value - minLong.value
  private val latRange = maxLat.value - minLat.value
  private val longStep = longRange / width.toDouble
  private val latStep = latRange / height.toDouble

  def apply(lat: Latitude, long: Longitude): T = {
    val latInd = ((maxLat.value-lat.value)/latStep).toInt
    val longInd = ((long.value+maxLong.value)/longStep).toInt
    val asSignedByte = data(latInd*width + longInd)
    val asUnsignedInt = asSignedByte & 0xFF
    normalizer(asUnsignedInt)
  }
}

object PNGGrid {
  def readElevations(resourceName: String, highestPoint: Double): PNGGrid[Elevation] = {
    val image = ImageIO.read(getClass.getResourceAsStream(resourceName))
    val data = image.getData.getDataBuffer.asInstanceOf[DataBufferByte].getData
    new PNGGrid(image.getWidth, image.getHeight, data, normalizer = (value: Int) => Elevation(value * highestPoint/256.0))
  }

  def readMassTypeMask(resourceName: String): PNGGrid[MassType] = {
    val image = ImageIO.read(getClass.getResourceAsStream(resourceName))
    val data = image.getData.getDataBuffer.asInstanceOf[DataBufferByte].getData
    val waterLandDetector: (Int) => MassType = {
      case 0 => Land
      case _ => Water
    }
    new PNGGrid(image.getWidth, image.getHeight, data, normalizer = waterLandDetector)
  }
}
