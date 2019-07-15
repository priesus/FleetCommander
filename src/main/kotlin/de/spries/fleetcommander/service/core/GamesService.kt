package de.spries.fleetcommander.service.core

import de.spries.fleetcommander.model.core.Game
import de.spries.fleetcommander.model.core.Player
import de.spries.fleetcommander.model.core.common.IllegalActionException
import de.spries.fleetcommander.model.facade.PlayerSpecificGame
import de.spries.fleetcommander.persistence.GameStore
import de.spries.fleetcommander.persistence.InvalidCodeException
import de.spries.fleetcommander.persistence.JoinCodeLimitReachedException
import de.spries.fleetcommander.persistence.JoinCodes
import de.spries.fleetcommander.service.core.dto.GameAccessParams
import de.spries.fleetcommander.service.core.dto.GameParams
import de.spries.fleetcommander.service.core.dto.GamePlayer
import de.spries.fleetcommander.service.core.dto.ShipFormationParams
import org.apache.logging.log4j.LogManager

class GamesService {

    fun createNewGame(playerName: String): GameAccessParams {
        val game = Game()
        val p = Player(playerName)
        game.addPlayer(p)

        val gameId = GameStore.INSTANCE.create(game)
        game.id = gameId
        val gamePlayer = GamePlayer.forIds(gameId, p.id)
        val authToken = GameAuthenticator.INSTANCE.createAuthToken(gamePlayer)

        LOGGER.debug("{}: Created for {}", gamePlayer, playerName)

        return GameAccessParams(gamePlayer, authToken)
    }

    @Throws(JoinCodeLimitReachedException::class)
    fun createJoinCode(gameId: Int): String {
        return JoinCodes.INSTANCE.create(gameId)
    }

    fun getActiveJoinCodes(gameId: Int): Collection<String> {
        return JoinCodes.INSTANCE[gameId]
    }

    @Throws(InvalidCodeException::class)
    fun joinGame(playerName: String, joinCode: String): GameAccessParams {
        val gameId = JoinCodes.INSTANCE.invalidate(joinCode)
        val game = GameStore.INSTANCE[gameId] ?: throw IllegalActionException("The game doesn't exist on the server")
        val player = Player(playerName)
        game.addPlayer(player)
        val gamePlayer = GamePlayer(gameId, player.id)
        val authToken = GameAuthenticator.INSTANCE.createAuthToken(gamePlayer)

        LOGGER.debug("{}: Joined by {}", gamePlayer, playerName)

        return GameAccessParams(gamePlayer, authToken)
    }

    fun getGame(gamePlayer: GamePlayer): PlayerSpecificGame {
        val game = GameStore.INSTANCE[gamePlayer.gameId]
        LOGGER.debug("{}: Get", gamePlayer)
        if (game != null) {
            val player = game.getPlayerWithId(gamePlayer.playerId)
            if (player != null) {
                return PlayerSpecificGame(game, player)
            }
            LOGGER.warn("{}: Get, but doesn't participate", gamePlayer)
            throw IllegalActionException("You're not participating in this game")
        }
        LOGGER.warn("{}: Get, but doesn't exist", gamePlayer)
        throw IllegalActionException("The game doesn't exist on the server")
    }

    fun quitGame(gamePlayer: GamePlayer) {
        LOGGER.debug("{}: Delete", gamePlayer)
        GameAuthenticator.INSTANCE.deleteAuthToken(gamePlayer)
        val game = getGame(gamePlayer)
        game.quit()
    }

    fun addComputerPlayer(gamePlayer: GamePlayer) {
        LOGGER.debug("{}: Add computer player", gamePlayer)
        val game = getGame(gamePlayer)
        game.addComputerPlayer()
    }

    fun modifyGame(gamePlayer: GamePlayer, params: GameParams) {
        LOGGER.debug("{}: Modify with params {}", gamePlayer, params)
        if (java.lang.Boolean.TRUE == params.isStarted) {
            val game = getGame(gamePlayer)
            game.start()
            JoinCodes.INSTANCE.invalidateAll(gamePlayer.gameId)
        }
    }

    fun endTurn(gamePlayer: GamePlayer) {
        LOGGER.debug("{}: End turn", gamePlayer)
        val game = getGame(gamePlayer)
        game.endTurn()
    }

    fun sendShips(gamePlayer: GamePlayer, ships: ShipFormationParams) {
        LOGGER.debug("{}: Send ships with params {}", gamePlayer, ships)
        val game = getGame(gamePlayer)
        game.universe!!.sendShips(ships.shipCount, ships.originPlanetId,
                ships.destinationPlanetId)
    }

    fun changePlanetProductionFocus(gamePlayer: GamePlayer, planetId: Int, focus: Int) {
        LOGGER.debug("{}: Change production focus of planet {} to {}", gamePlayer, planetId, focus)
        val game = getGame(gamePlayer)
        game.universe!!.getPlanet(planetId).changeProductionFocus(focus)
    }

    fun buildFactory(gamePlayer: GamePlayer, planetId: Int) {
        LOGGER.debug("{}: Build factory on planet {}", gamePlayer, planetId)
        val game = getGame(gamePlayer)
        game.universe!!.getPlanet(planetId).buildFactory()
    }

    companion object {

        private val LOGGER = LogManager.getLogger(GamesService::class.java.name)
    }
}