package de.spries.fleetcommander.model.core.universe

import kotlin.math.pow
import kotlin.math.sqrt

open class HasCoordinates(var x: Int, var y: Int) {

    fun distanceTo(other: HasCoordinates): Double {
        val distanceX = x - other.x
        val distanceY = y - other.y
        return sqrt(distanceX.toDouble().pow(2.0) + distanceY.toDouble().pow(2.0))
    }
}
