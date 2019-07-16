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
import mu.KotlinLogging

class GamesService {

    private val log = KotlinLogging.logger {}

    fun createNewGame(playerName: String): GameAccessParams {
        val game = Game()
        val p = Player(playerName)
        game.addPlayer(p)

        val gameId = GameStore.INSTANCE.create(game)
        game.assignId(gameId)
        val gamePlayer = GamePlayer(gameId, p.getId())
        val authToken = GameAuthenticator.INSTANCE.createAuthToken(gamePlayer)

        log.debug("{}: Created for {}", gamePlayer, playerName)

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
        val gamePlayer = GamePlayer(gameId, player.getId())
        val authToken = GameAuthenticator.INSTANCE.createAuthToken(gamePlayer)

        log.debug("{}: Joined by {}", gamePlayer, playerName)

        return GameAccessParams(gamePlayer, authToken)
    }

    fun getGame(gamePlayer: GamePlayer): PlayerSpecificGame {
        val game = GameStore.INSTANCE[gamePlayer.gameId]
        log.debug("{}: Get", gamePlayer)
        if (game != null) {
            val player = game.getPlayerWithId(gamePlayer.playerId)
            if (player != null) {
                return PlayerSpecificGame(game, player)
            }
            log.warn("{}: Get, but doesn't participate", gamePlayer)
            throw IllegalActionException("You're not participating in this game")
        }
        log.warn("{}: Get, but doesn't exist", gamePlayer)
        throw IllegalActionException("The game doesn't exist on the server")
    }

    fun quitGame(gamePlayer: GamePlayer) {
        log.debug("{}: Delete", gamePlayer)
        GameAuthenticator.INSTANCE.deleteAuthToken(gamePlayer)
        val game = getGame(gamePlayer)
        game.quit()
    }

    fun addComputerPlayer(gamePlayer: GamePlayer) {
        log.debug("{}: Add computer player", gamePlayer)
        val game = getGame(gamePlayer)
        game.addComputerPlayer()
    }

    fun modifyGame(gamePlayer: GamePlayer, params: GameParams) {
        log.debug("{}: Modify with params {}", gamePlayer, params)
        if (java.lang.Boolean.TRUE == params.isStarted) {
            val game = getGame(gamePlayer)
            game.start()
            JoinCodes.INSTANCE.invalidateAll(gamePlayer.gameId)
        }
    }

    fun endTurn(gamePlayer: GamePlayer) {
        log.debug("{}: End turn", gamePlayer)
        val game = getGame(gamePlayer)
        game.endTurn()
    }

    fun sendShips(gamePlayer: GamePlayer, ships: ShipFormationParams) {
        log.debug("{}: Send ships with params {}", gamePlayer, ships)
        val game = getGame(gamePlayer)
        game.getUniverse()!!.sendShips(ships.shipCount, ships.originPlanetId,
                ships.destinationPlanetId)
    }

    fun changePlanetProductionFocus(gamePlayer: GamePlayer, planetId: Int, focus: Int) {
        log.debug("{}: Change production focus of planet {} to {}", gamePlayer, planetId, focus)
        val game = getGame(gamePlayer)
        game.getUniverse()!!.getPlanet(planetId).changeProductionFocus(focus)
    }

    fun buildFactory(gamePlayer: GamePlayer, planetId: Int) {
        log.debug("{}: Build factory on planet {}", gamePlayer, planetId)
        val game = getGame(gamePlayer)
        game.getUniverse()!!.getPlanet(planetId).buildFactory()
    }
}
