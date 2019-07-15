package de.spries.fleetcommander.model.facade

import de.spries.fleetcommander.model.core.Player
import de.spries.fleetcommander.model.core.universe.HasCoordinates
import de.spries.fleetcommander.model.core.universe.Planet

data class PlayerSpecificPlanet(private val originalPlanet: Planet, private val viewingPlayer: Player)
    : HasCoordinates(originalPlanet.x, originalPlanet.y) {

    fun getId() = originalPlanet.id

    fun getPlanetClass() = if (isInhabitedByMe()) originalPlanet.planetClass.name else "?"

    fun isMyHomePlanet() = originalPlanet.isHomePlanetOf(viewingPlayer)

    fun isHomePlanet() = originalPlanet.isHomePlanetOf(viewingPlayer) || originalPlanet.isHomePlanet && isKnownAsEnemyPlanet()

    fun isInhabitedByMe() = originalPlanet.isInhabitedBy(viewingPlayer)

    fun isKnownAsEnemyPlanet() = if (TestMode.TEST_MODE && viewingPlayer.isHumanPlayer()) {
        originalPlanet.isInhabited() && !originalPlanet.isInhabitedBy(viewingPlayer)
    } else originalPlanet.isKnownAsEnemyPlanet(viewingPlayer)

    fun isUnderAttack() = if (isInhabitedByMe()) {
        originalPlanet.isUnderAttack
    } else false

    fun isJustInhabited() = if (isInhabitedByMe()) {
        originalPlanet.isJustInhabited
    } else false

    fun getShipCount() = if (isInhabitedByMe() || TestMode.TEST_MODE && viewingPlayer.isHumanPlayer()) {
        originalPlanet.getShipCount()
    } else 0

    fun getIncomingShipCount() = originalPlanet.getIncomingShipCount(viewingPlayer)

    fun getFactorySite() = if (isInhabitedByMe() || TestMode.TEST_MODE && viewingPlayer.isHumanPlayer()) {
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
}
