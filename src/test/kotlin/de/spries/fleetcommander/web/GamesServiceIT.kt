package de.spries.fleetcommander.web

import de.spries.fleetcommander.model.core.Game.Status
import de.spries.fleetcommander.model.core.common.IllegalActionException
import de.spries.fleetcommander.web.dto.GameParams
import de.spries.fleetcommander.web.dto.GamePlayer
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.empty
import org.hamcrest.Matchers.hasItem
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.notNullValue
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test

class GamesServiceIT {

    private lateinit var service: GamesService
    private lateinit var gamePlayer: GamePlayer

    @Before
    fun setUp() {
        service = GamesService(GameAuthenticator())
        val accessParams = service.createNewGame("Player 1")
        gamePlayer = GamePlayer(accessParams.getGameId(), accessParams.getPlayerId())
    }

    @Test
    fun canGetCreatedGame() {
        assertThat(service.getGame(gamePlayer), `is`(notNullValue()))
    }

    @Test
    fun canAddComputerPlayer() {
        assertThat(service.getGame(gamePlayer).getOtherPlayers(), hasSize(0))

        service.addComputerPlayer(gamePlayer)
        assertThat(service.getGame(gamePlayer).getOtherPlayers(), hasSize(1))
        assertThat(service.getGame(gamePlayer).getOtherPlayers()[0].getName(), `is`("Computer 1"))

        service.addComputerPlayer(gamePlayer)
        assertThat(service.getGame(gamePlayer).getOtherPlayers(), hasSize(2))
        assertThat(service.getGame(gamePlayer).getOtherPlayers()[1].getName(), `is`("Computer 2"))
    }

    @Test
    fun canStartGame() {
        service.addComputerPlayer(gamePlayer)
        assertThat(service.getGame(gamePlayer).getStatus(), `is`(Status.PENDING))

        service.modifyGame(gamePlayer, GameParams(false))
        assertThat(service.getGame(gamePlayer).getStatus(), `is`(Status.PENDING))

        service.modifyGame(gamePlayer, GameParams(false))
        assertThat(service.getGame(gamePlayer).getStatus(), `is`(Status.PENDING))

        service.modifyGame(gamePlayer, GameParams(true))
        assertThat(service.getGame(gamePlayer).getStatus(), `is`(Status.RUNNING))
    }

    @Test
    fun canJoinViaCreatedJoinCode() {
        val gameId = gamePlayer.gameId

        val joinCode = service.createJoinCode(gameId)
        assertThat(service.getActiveJoinCodes(gameId), hasItem(joinCode))

        val player2AccessParams = service.joinGame("Player 2", joinCode)
        assertThat(player2AccessParams.getGameId(), `is`(gameId))
        assertThat(service.getGame(gamePlayer).getOtherPlayers(), hasSize(1))
        assertThat(service.getGame(player2AccessParams.gamePlayer).getOtherPlayers(), hasSize(1))
    }

    @Test(expected = IllegalActionException::class)
    fun playerCannotAccessPendingGameAfterQuitting() {
        service.quitGame(gamePlayer)
        service.getGame(gamePlayer)
    }

    @Test
    fun startingGameInvalidatesAllJoinCodes() {
        service.addComputerPlayer(gamePlayer)
        val gameId = gamePlayer.gameId

        service.createJoinCode(gameId)
        service.createJoinCode(gameId)
        service.createJoinCode(gameId)

        val params = GameParams(true)
        service.modifyGame(gamePlayer, params)

        assertThat(service.getActiveJoinCodes(gameId), `is`(empty()))
    }
}
