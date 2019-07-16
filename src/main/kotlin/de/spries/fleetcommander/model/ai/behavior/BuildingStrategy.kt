package de.spries.fleetcommander.model.ai.behavior

import de.spries.fleetcommander.model.facade.PlayerSpecificUniverse

interface BuildingStrategy {

    fun buildFactories(universe: PlayerSpecificUniverse)

}
