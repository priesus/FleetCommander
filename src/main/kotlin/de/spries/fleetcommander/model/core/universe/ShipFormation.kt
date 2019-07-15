package de.spries.fleetcommander.model.core.universe

import de.spries.fleetcommander.model.core.Player
import de.spries.fleetcommander.model.core.common.IllegalActionException

data class ShipFormation(var shipCount: Int = 1, private val origin: Planet, private val destination: Planet, val commander: Player)
    : HasCoordinates(origin.x, origin.y) {

    private val distanceOverall: Double = origin.distanceTo(destination)
    var distanceTravelled = 0
        private set

    fun distanceRemaining() = distanceOverall - distanceTravelled

    init {
        if (shipCount <= 0) {
            throw IllegalActionException("Must send positive number of ships")
        }
    }

    fun canJoin(existingFormation: ShipFormation): Boolean {
        return (origin == existingFormation.origin
                && destination == existingFormation.destination
                && commander == existingFormation.commander
                && distanceTravelled == existingFormation.distanceTravelled)
    }

    fun join(existingFormation: ShipFormation) {
        if (!canJoin(existingFormation)) {
            throw IllegalArgumentException("cannot join this formation")
        }
        existingFormation.shipCount += shipCount
        shipCount = 0
    }

    fun travel() {
        distanceTravelled += DISTANCE_PER_TURN

        x = (origin.x + (destination.x - origin.x) * distanceTravelled / distanceOverall).toInt()
        y = (origin.y + (destination.y - origin.y) * distanceTravelled / distanceOverall).toInt()

        if (hasArrived()) {
            landOnDestination()
        }
    }

    fun landOnDestination() {
        destination.landShips(shipCount, commander)
        shipCount = 0
    }

    fun hasArrived(): Boolean {
        return distanceRemaining() <= 0
    }

    companion object {
        const val DISTANCE_PER_TURN = 8
    }
}
