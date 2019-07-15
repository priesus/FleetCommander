package de.spries.fleetcommander.model.core.universe

import de.spries.fleetcommander.model.core.Player

interface TurnEventBus {

    fun fireConqueredEnemyPlanet(invader: Player)

    fun fireConqueredUninhabitedPlanet(invader: Player)

    fun fireDefendedPlanet(inhabitant: Player)

    fun fireLostPlanet(previousInhabitant: Player)

    fun fireLostShipFormation(commander: Player)
}
