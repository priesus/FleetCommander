package de.spries.fleetcommander.model.facade

import de.spries.fleetcommander.model.core.Player
import de.spries.fleetcommander.model.core.universe.ShipFormation
import de.spries.fleetcommander.model.core.universe.Universe


class PlayerSpecificUniverse(private val originalUniverse: Universe, private val viewingPlayer: Player) {

    val planets: List<PlayerSpecificPlanet>
        get() = PlayerSpecificPlanet.convert(originalUniverse.planets, viewingPlayer)

    val homePlanet: PlayerSpecificPlanet?
        get() {
            val homePlanet = originalUniverse.getHomePlanetOf(viewingPlayer) ?: return null
            return PlayerSpecificPlanet.convert(homePlanet, viewingPlayer)
        }

    val travellingShipFormations: Collection<ShipFormation>
        get() = if (TestMode.TEST_MODE) originalUniverse.travellingShipFormations
        else originalUniverse.travellingShipFormations
                .filter { s -> s.commander === viewingPlayer }

    fun getPlanet(planetId: Int): PlayerSpecificPlanet {
        return PlayerSpecificPlanet.convert(originalUniverse.getPlanetForId(planetId), viewingPlayer)
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
