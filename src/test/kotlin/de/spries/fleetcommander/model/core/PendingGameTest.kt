package de.spries.fleetcommander.model.core

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import de.spries.fleetcommander.model.core.Game.Status
import de.spries.fleetcommander.model.core.common.IllegalActionException
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.hasItem
import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.nullValue
import org.junit.Assert.assertThat
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify

class PendingGameTest {

    private lateinit var game: Game
    private lateinit var gameWithPlayers: Game
    private lateinit var jack: Player
    private lateinit var john: Player
    private lateinit var computer: Player
    private lateinit var otherPlayer: Player

    @Before
    fun setUp() {
        john = mock()
        jack = mock()
        computer = mock()
        otherPlayer = mock()

        whenever(john.isHumanPlayer()).thenReturn(true)
        whenever(jack.isHumanPlayer()).thenReturn(true)
        whenever(computer.isHumanPlayer()).thenReturn(false)

        game = Game()
        game.addPlayer(john)
        gameWithPlayers = Game()
        gameWithPlayers.addPlayer(jack)
        gameWithPlayers.addPlayer(john)
        gameWithPlayers.addPlayer(computer)
    }

    @Test
    fun initialStatusIsPending() {
        assertThat(game.getStatus(), `is`(Status.PENDING))
    }

    @Test
    fun initialTurnNumberIsZero() {
        assertThat(game.getTurnNumber(), `is`(0))
    }

    @Test(expected = IllegalActionException::class)
    fun gameRequiresAtLeastTwoPlayersToStart() {
        game.start(john)
    }

    @Test
    fun playerIsAddedToPlayersList() {
        game.addPlayer(jack)
        assertThat<List<Player>>(game.getPlayers(), hasItem(jack))
    }

    @Test
    fun gameHasAMaximumOf6Players() {
        for (i in 0..4) {
            game.addPlayer(mock())
        }
        try {
            game.addPlayer(mock())
            fail("Expected exception")
        } catch (e: Exception) {
            assertThat(e.message, containsString("Limit of 6 players reached"))
        }

    }

    @Test
    fun cannotAddPlayerTwice() {
        val g = Game()
        g.addPlayer(Player("John"))
        try {
            g.addPlayer(Player("John"))
            fail("Excpected exception")
        } catch (e: IllegalActionException) {
            //Expected behavior
        }

    }

    @Test(expected = IllegalActionException::class)
    fun cannotEndTurnBeforeGameHasStarted() {
        game.endTurn()
    }

    @Test
    fun returnsPlayerWithSameId() {
        doReturn(1).`when`(jack).getId()
        doReturn(12).`when`(john).getId()
        assertThat(gameWithPlayers.getPlayerWithId(12), `is`(john))
    }

    @Test
    fun returnsNullForNonexistentPlayerId() {
        doReturn(1).`when`(jack).getId()
        doReturn(12).`when`(john).getId()
        assertThat(gameWithPlayers.getPlayerWithId(123), `is`(nullValue()))
    }

    @Test
    fun assignsIdToNewPlayers() {
        val g = Game()

        val p1 = mock<Player>()
        val p2 = mock<Player>()
        val p3 = mock<Player>()

        g.addPlayer(p1)
        g.addPlayer(p2)
        g.addPlayer(p3)

        verify(p1).assignId(1)
        verify(p2).assignId(2)
        verify(p3).assignId(3)
    }

    @Test
    fun playersAreNotifiedOfGameStart() {
        doReturn(true).`when`(john).isActive()
        doReturn(true).`when`(jack).isActive()
        gameWithPlayers.start()

        verify(john).notifyNewTurn(gameWithPlayers)
        verify(jack).notifyNewTurn(gameWithPlayers)
    }

    @Test
    fun quittingPlayerIsRemovedFromPendingGame() {
        gameWithPlayers.quit(john)
        assertThat<List<Player>>(gameWithPlayers.getPlayers(), not(hasItem(john)))
    }

    @Test(expected = IllegalActionException::class)
    fun nonParticipatingPlayerCannotQuitGame() {
        gameWithPlayers.quit(otherPlayer)
    }

    @Test
    fun gameHasNoUniverse() {
        assertThat(game.getUniverse(), `is`(nullValue()))
    }

    @Test(expected = IllegalActionException::class)
    fun playerThatDoesntParticipateCannotStartGame() {
        gameWithPlayers.start(otherPlayer)
    }

    @Test
    fun gameDoesntStartBeforeAllPlayersAreReady() {
        gameWithPlayers.start(john)
        assertThat(gameWithPlayers.getStatus(), `is`(Status.PENDING))
    }

    @Test
    fun gameStartsAfterAllPlayersAreReady() {
        doReturn(true).`when`(john).isReady()
        doReturn(true).`when`(jack).isReady()
        doReturn(true).`when`(computer).isReady()
        gameWithPlayers.start(jack)

        assertThat(gameWithPlayers.getStatus(), `is`(Status.RUNNING))
    }

}
