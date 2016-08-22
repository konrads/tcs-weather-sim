package com.tcs.weathersim.util

import scala.util.Random

trait Selector {
  def select(min: Double, max: Double, weights: Double*): Double = {
    assert(min < max, "min > max")
    (max - min) * aggWeight(weights) + min
  }

  def select(min: Int, max: Int, weights: Double*): Int = {
    assert(min < max, "min > max")
    ((max - min) * aggWeight(weights) + min).toInt
  }

  def select[T](left: T, right: T, weights: Double*): T = {
    if (aggWeight(weights) < 0.5) left else right
  }

  protected def aggWeight(weights: Seq[Double]): Double
}

object RepeatableSelector extends Selector {
  override protected def aggWeight(weights: Seq[Double]) = {
    assert(weights.forall(w => w>=0 && w<=1), s"weights ${weights.mkString(",")} must be [0,1]")
    weights.sum/weights.length
  }
}

class RandomSelector(randomFactor: Double = 0.2, seed: Long = System.currentTimeMillis()) extends Selector {
  assert(randomFactor > 0 && randomFactor <= 1, "random factor must be [0,1]")
  private val rnd = new Random(seed)

  override protected def aggWeight(weights: Seq[Double]) = {
    assert(weights.forall(w => w>=0 && w<=1), "weights must be [0,1]")
    val avgWeight = weights.sum/weights.length
    (rnd.nextDouble() - 0.5) * randomFactor + avgWeight
  }
}