package de.spries.fleetcommander.model.ai.behavior

import de.spries.fleetcommander.model.facade.PlayerSpecificUniverse

interface FleetStrategy {

    fun sendShips(universe: PlayerSpecificUniverse)
}
