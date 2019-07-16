package de.spries.fleetcommander.model.ai.behavior

import de.spries.fleetcommander.model.facade.PlayerSpecificUniverse

class SearchEnemyHomePlanetFleetStrategy : FleetStrategy {

    override fun sendShips(universe: PlayerSpecificUniverse) {
        val homePlanet = universe.getHomePlanet()
        val allPlanets = universe.getPlanets()
        val myPlanets = allPlanets.filter { p -> p.isInhabitedByMe() }
        val enemyHomePlanet = allPlanets.firstOrNull { p -> p.isKnownAsEnemyPlanet() && p.isHomePlanet() }
        val unknownPlanets = allPlanets
                .filter { p -> !p.isInhabitedByMe() && !p.isKnownAsEnemyPlanet() && p.getIncomingShipCount() == 0 }
                .sortedBy { it.distanceTo(homePlanet!!) }
                .toList()

        // Expand to next closest uninhabited planets
        for (unknownPlanet in unknownPlanets) {
            // send 1 ship from closest of my own planets which has ships > 0
            val closestOwnPlanetWhichHasShips = myPlanets
                    .sortedBy { it.distanceTo(unknownPlanet) }
                    .firstOrNull { p -> p.getShipCount() > 0 } ?: break

            universe.sendShips(1, closestOwnPlanetWhichHasShips.getId(), unknownPlanet.getId())
        }

        if (enemyHomePlanet != null) {
            // Full attack on enemy home planet
            myPlanets
                    .filter { p -> p.getShipCount() > 0 }
                    .forEach { p -> universe.sendShips(p.getShipCount(), p.getId(), enemyHomePlanet.getId()) }
        }
    }
}
