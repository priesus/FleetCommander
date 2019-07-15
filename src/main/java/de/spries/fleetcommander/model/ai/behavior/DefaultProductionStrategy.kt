package de.spries.fleetcommander.model.ai.behavior

import de.spries.fleetcommander.model.core.universe.FactorySite
import de.spries.fleetcommander.model.facade.PlayerSpecificPlanet
import de.spries.fleetcommander.model.facade.PlayerSpecificUniverse

class DefaultProductionStrategy : ProductionStrategy {

    override fun updateProductionFocus(universe: PlayerSpecificUniverse, availablePlayerCredits: Int) {
        val allPlanets = universe.planets
        val myPlanets = PlayerSpecificPlanet.filterMyPlanets(allPlanets)
                .sortedBy { it.distanceTo(universe.homePlanet!!) }

        val openFactorySlots = myPlanets.filter { p -> p.factorySite!!.hasAvailableSlots() }.count()
        val unknownPlanets = allPlanets.filter { p -> !p.isInhabitedByMe && !p.isKnownAsEnemyPlanet }.count()

        val potentialOpenFactorySlots = openFactorySlots + unknownPlanets * FactorySite.FACTORY_SLOTS
        val maxFactoryCost = potentialOpenFactorySlots * FactorySite.FACTORY_COST

        if (potentialOpenFactorySlots == 0 || availablePlayerCredits > maxFactoryCost) {
            // There are no more potential factory slots --> focus on attack
            myPlanets.forEach { p -> p.changeProductionFocus(FULL_SHIP_BUILDING_FOCUS) }
        } else {
            myPlanets.forEach { p -> p.changeProductionFocus(BALANCED_BUILDING_FOCUS) }

            myPlanets
                    .filter { p1 -> p1.shipCount > 5 }
                    .toList()
                    .forEach { p -> p.changeProductionFocus(FULL_MONEY_BUILDING_FOCUS) }
        }
    }

    companion object {

        private const val FULL_SHIP_BUILDING_FOCUS = FactorySite.MAX_PRODUCTION_FOCUS
        private const val BALANCED_BUILDING_FOCUS = FULL_SHIP_BUILDING_FOCUS / 2
        private const val FULL_MONEY_BUILDING_FOCUS = 0
    }
}
