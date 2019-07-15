package de.spries.fleetcommander.model.ai.behavior

import de.spries.fleetcommander.model.facade.PlayerSpecificPlanet
import de.spries.fleetcommander.model.facade.PlayerSpecificUniverse

class DefaultBuildingStrategy : BuildingStrategy {

    override fun buildFactories(universe: PlayerSpecificUniverse) {
        val allPlanets = universe.planets
        val myPlanets = PlayerSpecificPlanet.filterMyPlanets(allPlanets)
                .sortedBy { it.distanceTo(universe.homePlanet!!) }

        for (planet in myPlanets) {
            while (planet.canBuildFactory()) {
                planet.buildFactory()
            }
        }
    }
}
