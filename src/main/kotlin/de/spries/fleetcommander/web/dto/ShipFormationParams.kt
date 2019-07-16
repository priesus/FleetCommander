package de.spries.fleetcommander.web.dto

data class ShipFormationParams(val shipCount: Int = 0, val originPlanetId: Int = 0, val destinationPlanetId: Int = 0) {

    override fun toString(): String {
        return "ShipFormationParams($shipCount from $originPlanetId to $destinationPlanetId)"
    }
}