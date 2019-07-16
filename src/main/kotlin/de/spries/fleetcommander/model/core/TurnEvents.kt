package de.spries.fleetcommander.model.core

import de.spries.fleetcommander.model.core.universe.TurnEventBus

open class TurnEvents(players: Collection<Player>) : TurnEventBus {

    private val events: MutableMap<Player, PlayerTurnEvents> = mutableMapOf()

    inner class PlayerTurnEvents {

        var conqueredEnemyPlanets = 0
        var conqueredUninhabitedPlanets = 0
        var defendedPlanets = 0
        var lostShipFormations = 0
        var lostPlanets = 0

        fun reset() {
            conqueredEnemyPlanets = 0
            conqueredUninhabitedPlanets = 0
            defendedPlanets = 0
            lostShipFormations = 0
            lostPlanets = 0
        }

        fun hasEvents(): Boolean {
            val sumEvents = (conqueredEnemyPlanets + conqueredUninhabitedPlanets + defendedPlanets + lostShipFormations
                    + lostPlanets)
            return 0 < sumEvents
        }

    }

    init {
        players.forEach { p -> events[p] = PlayerTurnEvents() }
    }

    fun clear() {
        events.forEach { (_, playerEvents) -> playerEvents.reset() }
    }

    fun hasEvents(viewingPlayer: Player): Boolean {
        return events[viewingPlayer]!!.hasEvents()
    }

    fun getConqueredEnemyPlanets(affectedPlayer: Player): Int {
        return events[affectedPlayer]!!.conqueredEnemyPlanets
    }

    fun getConqueredUninhabitedPlanets(affectedPlayer: Player): Int {
        return events[affectedPlayer]!!.conqueredUninhabitedPlanets
    }

    fun getDefendedPlanets(affectedPlayer: Player): Int {
        return events[affectedPlayer]!!.defendedPlanets
    }

    fun getLostShipFormations(affectedPlayer: Player): Int {
        return events[affectedPlayer]!!.lostShipFormations
    }

    fun getLostPlanets(affectedPlayer: Player): Int {
        return events[affectedPlayer]!!.lostPlanets
    }

    override fun fireConqueredEnemyPlanet(invader: Player) {
        events[invader]!!.conqueredEnemyPlanets++
    }

    override fun fireConqueredUninhabitedPlanet(invader: Player) {
        events[invader]!!.conqueredUninhabitedPlanets++
    }

    override fun fireDefendedPlanet(inhabitant: Player) {
        events[inhabitant]!!.defendedPlanets++
    }

    override fun fireLostShipFormation(commander: Player) {
        events[commander]!!.lostShipFormations++
    }

    override fun fireLostPlanet(previousInhabitant: Player) {
        events[previousInhabitant]!!.lostPlanets++
    }
}
