package de.spries.fleetcommander.model.facade

import de.spries.fleetcommander.model.core.Player
import de.spries.fleetcommander.model.core.TurnEvents

class PlayerSpecificTurnEvents(private val originalEvents: TurnEvents, private val viewingPlayer: Player) {

    fun getConqueredEnemyPlanets() = originalEvents.getConqueredEnemyPlanets(viewingPlayer)

    fun getConqueredUninhabitedPlanets() = originalEvents.getConqueredUninhabitedPlanets(viewingPlayer)

    fun getLostShipFormations() = originalEvents.getLostShipFormations(viewingPlayer)

    fun getDefendedPlanets() = originalEvents.getDefendedPlanets(viewingPlayer)

    fun getLostPlanets() = originalEvents.getLostPlanets(viewingPlayer)

    fun hasEvents() = originalEvents.hasEvents(viewingPlayer)
}
