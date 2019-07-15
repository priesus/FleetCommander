package de.spries.fleetcommander.model.facade

import de.spries.fleetcommander.model.ai.ComputerPlayer
import de.spries.fleetcommander.model.ai.behavior.DefaultBuildingStrategy
import de.spries.fleetcommander.model.ai.behavior.DefaultProductionStrategy
import de.spries.fleetcommander.model.ai.behavior.SearchEnemyHomePlanetFleetStrategy
import de.spries.fleetcommander.model.core.Game
import de.spries.fleetcommander.model.core.Game.Status
import de.spries.fleetcommander.model.core.Player

class PlayerSpecificGame(private val originalGame: Game, private val viewingPlayer: Player) {

    val id: Int
        get() = originalGame.id

    val status: Status
        get() = originalGame.status

    val turnNumber: Int
        get() = originalGame.turnNumber

    val previousTurnEvents: PlayerSpecificTurnEvents?
        get() {
            val events = originalGame.previousTurnEvents
            return if (events != null) {
                PlayerSpecificTurnEvents(events, viewingPlayer)
            } else null
        }

    val universe: PlayerSpecificUniverse?
        get() = if (originalGame.universe != null) {
            PlayerSpecificUniverse.convert(originalGame.universe!!, viewingPlayer)
        } else null

    val me: OwnPlayer
        get() = OwnPlayer(viewingPlayer)

    val otherPlayers: Collection<OtherPlayer>
        get() {
            val otherOriginalPlayers = Player.filterAllOtherPlayers(originalGame.players, viewingPlayer)
            return OtherPlayer.convert(otherOriginalPlayers)
        }

    fun addComputerPlayer() {
        val numComputerPlayers = otherPlayers.stream().filter { p -> !p.isHumanPlayer }.count()
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
