package de.spries.fleetcommander.model.ai.behavior

import de.spries.fleetcommander.model.facade.PlayerSpecificPlanet
import de.spries.fleetcommander.model.facade.PlayerSpecificUniverse

class SearchEnemyHomePlanetFleetStrategy : FleetStrategy {

    override fun sendShips(universe: PlayerSpecificUniverse) {
        val homePlanet = universe.homePlanet
        val allPlanets = universe.planets
        val myPlanets = PlayerSpecificPlanet.filterMyPlanets(allPlanets)
        val enemyHomePlanet = allPlanets.firstOrNull { p -> p.isKnownAsEnemyPlanet && p.isHomePlanet }
        val unknownPlanets = allPlanets
                .filter { p -> !p.isInhabitedByMe && !p.isKnownAsEnemyPlanet && p.incomingShipCount == 0 }
                .sortedBy { it.distanceTo(homePlanet) }
                .toList()

        // Expand to next closest uninhabited planets
        for (unknownPlanet in unknownPlanets) {
            // send 1 ship from closest of my own planets which has ships > 0
            val closestOwnPlanetWhichHasShips = myPlanets
                    .sortedBy { it.distanceTo(unknownPlanet) }
                    .firstOrNull { p -> p.shipCount > 0 } ?: break

            universe.sendShips(1, closestOwnPlanetWhichHasShips.id, unknownPlanet.id)
        }

        if (enemyHomePlanet != null) {
            // Full attack on enemy home planet
            myPlanets
                    .filter { p -> p.shipCount > 0 }
                    .forEach { p -> universe.sendShips(p.shipCount, p.id, enemyHomePlanet.id) }
        }
    }

}
