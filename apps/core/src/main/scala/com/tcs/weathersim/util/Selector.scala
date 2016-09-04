package com.tcs.weathersim.util

import scala.util.Random

/**
  * Mechanism for selection between either a range of Ints/Doubles, or 2 values of same type.
  * The selection is based on average of the passed in weights (values [0.0, 1.0]). The closer the average is to 0.0 -
  * the more likelihood the returned value will be closer to min/left; the closer to 1.0 - the more likelihood the
  * returned value will be closer to max/right.
  */
trait Selector {
  /**
    * Return Double between min and max, with lower weights leaning towards min, higher towards max.
    */
  def select(min: Double, max: Double, weights: Double*): Double = {
    assert(min < max, "min > max")
    (max - min) * aggWeight(weights) + min
  }

  /**
    * Return Int between min and max, with lower weights leaning towards min, higher towards max.
    */
  def select(min: Int, max: Int, weights: Double*): Int = {
    assert(min < max, "min > max")
    ((max - min) * aggWeight(weights) + min).toInt
  }

  /**
    * Return Double between min and max, with lower weights leaning towards left, higher towards right.
    */
  def select[T](left: T, right: T, weights: Double*): T = {
    if (aggWeight(weights) < 0.5) left else right
  }

  protected def aggWeight(weights: Seq[Double]): Double
}

/**
  * Selector that always returns the same results.
  */
object RepeatableSelector extends Selector {
  override protected def aggWeight(weights: Seq[Double]) = {
    assert(weights.forall(w => w>=0 && w<=1), s"weights ${weights.mkString(",")} must be [0,1]")
    weights.sum/weights.length
  }
}

/**
  * Selector adjusting the weight average by randomFactor (default to +/- 10%).
  */
class RandomSelector(randomFactor: Double = 0.2, seed: Long = System.currentTimeMillis()) extends Selector {
  assert(randomFactor > 0 && randomFactor <= 1, "random factor must be [0,1]")
  private val rnd = new Random(seed)

  override protected def aggWeight(weights: Seq[Double]) = {
    assert(weights.forall(w => w>=0 && w<=1), "weights must be [0,1]")
    val avgWeight = weights.sum/weights.length
    (rnd.nextDouble() - 0.5) * randomFactor + avgWeight
  }
}