package de.spries.fleetcommander.model.facade

import de.spries.fleetcommander.model.ai.ComputerPlayer
import de.spries.fleetcommander.model.ai.behavior.DefaultBuildingStrategy
import de.spries.fleetcommander.model.ai.behavior.DefaultProductionStrategy
import de.spries.fleetcommander.model.ai.behavior.SearchEnemyHomePlanetFleetStrategy
import de.spries.fleetcommander.model.core.Game
import de.spries.fleetcommander.model.core.Player

open class PlayerSpecificGame(private val originalGame: Game, private val viewingPlayer: Player) {

    fun getId() = originalGame.id

    fun getStatus() = originalGame.status

    fun getTurnNumber() = originalGame.turnNumber

    fun getPreviousTurnEvents(): PlayerSpecificTurnEvents? {
        val events = originalGame.previousTurnEvents
        return if (events != null) {
            PlayerSpecificTurnEvents(events, viewingPlayer)
        } else null
    }

    fun getUniverse() = if (originalGame.universe != null) {
        PlayerSpecificUniverse.convert(originalGame.universe!!, viewingPlayer)
    } else null

    fun getMe() = OwnPlayer(viewingPlayer)

    fun getOtherPlayers() = Player.filterAllOtherPlayers(originalGame.players, viewingPlayer)
            .map { OtherPlayer(it) }

    fun addComputerPlayer() {
        val numComputerPlayers = getOtherPlayers().count { p -> !p.isHumanPlayer() }
        val name = "Computer " + (numComputerPlayers + 1)
        val player = ComputerPlayer(name, DefaultBuildingStrategy(), SearchEnemyHomePlanetFleetStrategy(), DefaultProductionStrategy())
        originalGame.addPlayer(player)
    }

    fun addHumanPlayer(playerName: String) {
        originalGame.addPlayer(Player(playerName))
    }

    fun start() {
        originalGame.start(viewingPlayer)
    }

    fun endTurn() {
        originalGame.endTurn(viewingPlayer)
    }

    fun quit() {
        originalGame.quit(viewingPlayer)
    }
}
