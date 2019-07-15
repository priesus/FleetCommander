package de.spries.fleetcommander.model.ai.behavior

import de.spries.fleetcommander.model.facade.PlayerSpecificPlanet
import de.spries.fleetcommander.model.facade.PlayerSpecificUniverse

class AggressiveFleetStrategy : FleetStrategy {

    override fun sendShips(universe: PlayerSpecificUniverse) {
        val homePlanet = universe.homePlanet!!
        val allPlanets = universe.planets
        val myPlanets = PlayerSpecificPlanet.filterMyPlanets(allPlanets)
        val enemyPlanets = allPlanets
                .filter { p -> p.isKnownAsEnemyPlanet }

        if (enemyPlanets.isEmpty()) {
            val unknownPlanets = allPlanets
                    .filter { p -> !p.isInhabitedByMe && p.incomingShipCount == 0 }
                    .sortedBy { p -> p.distanceTo(homePlanet) }

            val numPlanetsToInvade = Math.min(unknownPlanets.size, homePlanet.shipCount)
            val planetsToInvade = unknownPlanets.subList(0, numPlanetsToInvade)
            for (unknownPlanet in planetsToInvade) {
                universe.sendShips(1, homePlanet.id, unknownPlanet.id)
            }
        } else {
            val someEnemyPlanet = enemyPlanets[0]
            for (planet in myPlanets) {
                val ships = planet.shipCount
                if (ships > 0) {
                    universe.sendShips(ships, planet.id, someEnemyPlanet.id)
                }
            }
        }
    }

}
