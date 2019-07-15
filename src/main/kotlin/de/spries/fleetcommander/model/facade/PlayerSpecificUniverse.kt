package de.spries.fleetcommander.model.facade

import de.spries.fleetcommander.model.core.Player
import de.spries.fleetcommander.model.core.universe.ShipFormation
import de.spries.fleetcommander.model.core.universe.Universe


open class PlayerSpecificUniverse(private val originalUniverse: Universe, private val viewingPlayer: Player) {

    fun getPlanets(): List<PlayerSpecificPlanet> {
        return originalUniverse.planets.map { PlayerSpecificPlanet(it, viewingPlayer) }
    }

    fun getHomePlanet(): PlayerSpecificPlanet? {
        val homePlanet = originalUniverse.getHomePlanetOf(viewingPlayer) ?: return null
        return PlayerSpecificPlanet(homePlanet, viewingPlayer)
    }

    fun getTravellingShipFormations(): Collection<ShipFormation> {
        return if (TestMode.TEST_MODE) originalUniverse.travellingShipFormations
        else originalUniverse.travellingShipFormations
                .filter { s -> s.commander === viewingPlayer }
    }

    fun getPlanet(planetId: Int): PlayerSpecificPlanet {
        return PlayerSpecificPlanet(originalUniverse.getPlanetForId(planetId), viewingPlayer)
    }

    fun sendShips(shipCount: Int, originPlanetId: Int, destinationPlanetId: Int) {
        originalUniverse.sendShips(shipCount, originPlanetId, destinationPlanetId, viewingPlayer)
    }

    companion object {

        fun convert(universe: Universe, viewingPlayer: Player): PlayerSpecificUniverse {
            return PlayerSpecificUniverse(universe, viewingPlayer)
        }
    }
}
