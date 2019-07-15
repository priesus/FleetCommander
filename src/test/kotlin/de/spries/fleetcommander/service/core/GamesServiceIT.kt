package de.spries.fleetcommander

import de.spries.fleetcommander.model.core.Game.Status
import de.spries.fleetcommander.model.core.common.IllegalActionException
import de.spries.fleetcommander.service.core.dto.GameParams
import de.spries.fleetcommander.service.core.dto.GamePlayer
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.empty
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
        service = GamesService()
        val accessParams = service.createNewGame("Player 1")
        gamePlayer = GamePlayer.forIds(accessParams.gameId, accessParams.playerId)
    }

    @Test
    fun canGetCreatedGame() {
        assertThat(service.getGame(gamePlayer), `is`(notNullValue()))
    }

    @Test
    fun canAddComputerPlayer() {
        assertThat(service.getGame(gamePlayer).otherPlayers, hasSize(0))

        service.addComputerPlayer(gamePlayer)
        assertThat(service.getGame(gamePlayer).otherPlayers, hasSize(1))
        assertThat(service.getGame(gamePlayer).otherPlayers[0].name, `is`("Computer 1"))

        service.addComputerPlayer(gamePlayer)
        assertThat(service.getGame(gamePlayer).otherPlayers, hasSize(2))
        assertThat(service.getGame(gamePlayer).otherPlayers[1].name, `is`("Computer 2"))
    }

    @Test
    fun canStartGame() {
        service.addComputerPlayer(gamePlayer)
        assertThat(service.getGame(gamePlayer).status, `is`(Status.PENDING))

        service.modifyGame(gamePlayer, GameParams(false))
        assertThat(service.getGame(gamePlayer).status, `is`(Status.PENDING))

        service.modifyGame(gamePlayer, GameParams(false))
        assertThat(service.getGame(gamePlayer).status, `is`(Status.PENDING))

        service.modifyGame(gamePlayer, GameParams(true))
        assertThat(service.getGame(gamePlayer).status, `is`(Status.RUNNING))
    }

    @Test
    fun canJoinViaCreatedJoinCode() {
        val gameId = gamePlayer.gameId

        val joinCode = service.createJoinCode(gameId)
        assertThat(service.getActiveJoinCodes(gameId), contains(joinCode))

        val player2AccessParams = service.joinGame("Player 2", joinCode)
        assertThat(player2AccessParams.gameId, `is`(gameId))
        assertThat(service.getGame(gamePlayer).otherPlayers, hasSize(1))
        assertThat(service.getGame(player2AccessParams.gamePlayer).otherPlayers, hasSize(1))
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
