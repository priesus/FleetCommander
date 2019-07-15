package de.spries.fleetcommander.model.facade

import de.spries.fleetcommander.model.core.Player
import de.spries.fleetcommander.model.core.universe.FactorySite
import de.spries.fleetcommander.model.core.universe.HasCoordinates
import de.spries.fleetcommander.model.core.universe.Planet

data class PlayerSpecificPlanet(private val originalPlanet: Planet, private val viewingPlayer: Player)
    : HasCoordinates(originalPlanet.x, originalPlanet.y) {

    val id: Int
        get() = originalPlanet.id

    val planetClass: String
        get() = if (isInhabitedByMe) originalPlanet.planetClass.name else "?"

    val isMyHomePlanet: Boolean
        get() = originalPlanet.isHomePlanetOf(viewingPlayer)

    val isHomePlanet: Boolean
        get() = originalPlanet.isHomePlanetOf(viewingPlayer) || originalPlanet.isHomePlanet && isKnownAsEnemyPlanet

    val isInhabitedByMe: Boolean
        get() = originalPlanet.isInhabitedBy(viewingPlayer)

    val isKnownAsEnemyPlanet: Boolean
        get() = if (TestMode.TEST_MODE && viewingPlayer.isHumanPlayer()) {
            originalPlanet.isInhabited && !originalPlanet.isInhabitedBy(viewingPlayer)
        } else originalPlanet.isKnownAsEnemyPlanet(viewingPlayer)

    val isUnderAttack: Boolean
        get() = if (isInhabitedByMe) {
            originalPlanet.isUnderAttack
        } else false

    val isJustInhabited: Boolean
        get() = if (isInhabitedByMe) {
            originalPlanet.isJustInhabited
        } else false

    val shipCount: Int
        get() = if (isInhabitedByMe || TestMode.TEST_MODE && viewingPlayer.isHumanPlayer()) {
            originalPlanet.getShipCount()
        } else 0

    val incomingShipCount: Int
        get() = originalPlanet.getIncomingShipCount(viewingPlayer)

    val factorySite: FactorySite?
        get() = if (isInhabitedByMe || TestMode.TEST_MODE && viewingPlayer.isHumanPlayer()) {
            originalPlanet.factorySite
        } else null

    fun canBuildFactory(): Boolean {
        return originalPlanet.canBuildFactory(viewingPlayer)
    }

    fun buildFactory() {
        originalPlanet.buildFactory(viewingPlayer)
    }

    fun changeProductionFocus(focus: Int) {
        originalPlanet.setProductionFocus(focus, viewingPlayer)
    }

    companion object {

        fun filterMyPlanets(allPlanets: List<PlayerSpecificPlanet>): List<PlayerSpecificPlanet> {
            return allPlanets.filter { p -> p.isInhabitedByMe }
        }

        fun convert(planet: Planet, viewingPlayer: Player): PlayerSpecificPlanet {
            return PlayerSpecificPlanet(planet, viewingPlayer)
        }

        fun convert(planets: List<Planet>, viewingPlayer: Player): List<PlayerSpecificPlanet> {
            return planets.map { p -> convert(p, viewingPlayer) }
        }
    }
}
