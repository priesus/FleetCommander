package de.spries.fleetcommander.model.ai.behavior

import de.spries.fleetcommander.model.facade.PlayerSpecificUniverse

open class DefaultBuildingStrategy : BuildingStrategy {

    override fun buildFactories(universe: PlayerSpecificUniverse) {
        val allPlanets = universe.getPlanets()
        val myPlanets = allPlanets.filter { p -> p.isInhabitedByMe() }
                .sortedBy { it.distanceTo(universe.getHomePlanet()!!) }

        for (planet in myPlanets) {
            while (planet.canBuildFactory()) {
                planet.buildFactory()
            }
        }
    }
}
