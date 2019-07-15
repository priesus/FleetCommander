package de.spries.fleetcommander.model.ai.behavior

import de.spries.fleetcommander.model.facade.PlayerSpecificUniverse

open class AggressiveFleetStrategy : FleetStrategy {

    override fun sendShips(universe: PlayerSpecificUniverse) {
        val homePlanet = universe.getHomePlanet()!!
        val allPlanets = universe.getPlanets()
        val myPlanets = allPlanets.filter { p -> p.isInhabitedByMe() }
        val enemyPlanets = allPlanets
                .filter { p -> p.isKnownAsEnemyPlanet() }

        if (enemyPlanets.isEmpty()) {
            val unknownPlanets = allPlanets
                    .filter { p -> !p.isInhabitedByMe() && p.getIncomingShipCount() == 0 }
                    .sortedBy { p -> p.distanceTo(homePlanet) }

            val numPlanetsToInvade = Math.min(unknownPlanets.size, homePlanet.getShipCount())
            val planetsToInvade = unknownPlanets.subList(0, numPlanetsToInvade)
            for (unknownPlanet in planetsToInvade) {
                universe.sendShips(1, homePlanet.getId(), unknownPlanet.getId())
            }
        } else {
            val someEnemyPlanet = enemyPlanets[0]
            for (planet in myPlanets) {
                val ships = planet.getShipCount()
                if (ships > 0) {
                    universe.sendShips(ships, planet.getId(), someEnemyPlanet.getId())
                }
            }
        }
    }
}
