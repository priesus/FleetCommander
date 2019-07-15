package de.spries.fleetcommander.model.facade

import de.spries.fleetcommander.model.core.Player
import de.spries.fleetcommander.model.core.TurnEvents

class PlayerSpecificTurnEvents(private val originalEvents: TurnEvents, private val viewingPlayer: Player) {

    val conqueredEnemyPlanets: Int
        get() = originalEvents.getConqueredEnemyPlanets(viewingPlayer)

    val conqueredUninhabitedPlanets: Int
        get() = originalEvents.getConqueredUninhabitedPlanets(viewingPlayer)

    val lostShipFormations: Int
        get() = originalEvents.getLostShipFormations(viewingPlayer)

    val defendedPlanets: Int
        get() = originalEvents.getDefendedPlanets(viewingPlayer)

    val lostPlanets: Int
        get() = originalEvents.getLostPlanets(viewingPlayer)

    val hasEvents: Boolean
        get() = originalEvents.hasEvents(viewingPlayer)
}
