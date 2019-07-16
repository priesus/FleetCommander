package de.spries.fleetcommander.model.facade

import de.spries.fleetcommander.model.ai.ComputerPlayer
import de.spries.fleetcommander.model.ai.behavior.DefaultBuildingStrategy
import de.spries.fleetcommander.model.ai.behavior.DefaultProductionStrategy
import de.spries.fleetcommander.model.ai.behavior.SearchEnemyHomePlanetFleetStrategy
import de.spries.fleetcommander.model.core.Game
import de.spries.fleetcommander.model.core.Player

open class PlayerSpecificGame(private val originalGame: Game, private val viewingPlayer: Player) {

    fun getId() = originalGame.getId()

    fun getStatus() = originalGame.getStatus()

    fun getTurnNumber() = originalGame.getTurnNumber()

    fun getPreviousTurnEvents(): PlayerSpecificTurnEvents? {
        val events = originalGame.getPreviousTurnEvents()
        return if (events != null) {
            PlayerSpecificTurnEvents(events, viewingPlayer)
        } else null
    }

    fun getUniverse() = if (originalGame.getUniverse() != null) {
        PlayerSpecificUniverse.convert(originalGame.getUniverse()!!, viewingPlayer)
    } else null

    fun getMe() = OwnPlayer(viewingPlayer)

    fun getOtherPlayers() = Player.filterAllOtherPlayers(originalGame.getPlayers(), viewingPlayer)
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
