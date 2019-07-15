package de.spries.fleetcommander.model.ai

import de.spries.fleetcommander.model.ai.behavior.BuildingStrategy
import de.spries.fleetcommander.model.ai.behavior.FleetStrategy
import de.spries.fleetcommander.model.ai.behavior.ProductionStrategy
import de.spries.fleetcommander.model.core.Game
import de.spries.fleetcommander.model.core.Player
import de.spries.fleetcommander.model.facade.PlayerSpecificGame
import mu.KotlinLogging

class ComputerPlayer(name: String, private val buildingStrategy: BuildingStrategy, private val fleetStrategy: FleetStrategy, private val productionStrategy: ProductionStrategy)
    : Player(name) {

    private val log = KotlinLogging.logger {}

    init {
        setReady()
    }

    override fun isHumanPlayer(): Boolean {
        return false
    }

    override fun notifyNewTurn(game: Game) {
        try {
            playTurn(PlayerSpecificGame(game, this))
        } catch (e: Exception) {
            // Just end the turn (still it shouldn't happen)
            log.warn("Game ${game.id}: Computer player '$name' caused an exception", e)
        }

        game.endTurn(this)
    }

    fun playTurn(game: PlayerSpecificGame) {
        val universe = game.universe
        fleetStrategy.sendShips(universe!!)
        buildingStrategy.buildFactories(universe)
        productionStrategy.updateProductionFocus(universe, credits)
    }
}
