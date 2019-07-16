package de.spries.fleetcommander.model.ai.behavior

import de.spries.fleetcommander.model.facade.PlayerSpecificUniverse

interface ProductionStrategy {

    fun updateProductionFocus(universe: PlayerSpecificUniverse, availablePlayerCredits: Int)
}
