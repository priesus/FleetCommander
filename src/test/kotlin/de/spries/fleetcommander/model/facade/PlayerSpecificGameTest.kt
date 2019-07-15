package de.spries.fleetcommander.model.facade

import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.nullValue
import org.junit.Assert.assertThat
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

import java.util.Arrays

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor

import de.spries.fleetcommander.model.core.Game
import de.spries.fleetcommander.model.core.Player

class PlayerSpecificGameTest {

    private lateinit var originalGame: Game
    private lateinit var self: Player
    private lateinit var otherPlayer: Player
    private lateinit var computerPlayer: Player
    private lateinit var ownGame: PlayerSpecificGame

    @Before
    fun setUp() {
        originalGame = mock(Game::class.java)
        self = mock(Player::class.java)
        otherPlayer = mock(Player::class.java)
        computerPlayer = mock(Player::class.java)

        doReturn(true).`when`(self).isHumanPlayer()
        doReturn(true).`when`(otherPlayer).isHumanPlayer()
        doReturn(false).`when`(computerPlayer).isHumanPlayer()

        doReturn("Myself").`when`(self).name

        doReturn(Arrays.asList(self, otherPlayer)).`when`(originalGame).players
        ownGame = PlayerSpecificGame(originalGame!!, self!!)
    }

    @Test
    fun forwardsCallToGetId() {
        ownGame!!.id
        verify(originalGame).id
    }

    @Test
    @Throws(Exception::class)
    fun addsComputerPlayerWithName() {
        ownGame!!.addComputerPlayer()

        doReturn(Arrays.asList(self, otherPlayer, computerPlayer)).`when`(originalGame).players
        ownGame!!.addComputerPlayer()

        val argument = ArgumentCaptor.forClass(Player::class.java)
        verify(originalGame, times(2)).addPlayer(argument.capture())
        assertThat(argument.allValues[0].name, `is`("Computer 1"))
        assertThat(argument.allValues[1].name, `is`("Computer 2"))
    }

    @Test
    @Throws(Exception::class)
    fun addsHumanPlayerWithName() {
        ownGame!!.addHumanPlayer("Player 2")

        val argument = ArgumentCaptor.forClass(Player::class.java)
        verify(originalGame).addPlayer(argument.capture())
        assertThat(argument.value.name, `is`("Player 2"))
    }

    @Test
    fun forwardsCallToStart() {
        ownGame!!.start()
        verify(originalGame).start(self!!)
    }

    @Test
    fun forwardsCallToGetStatus() {
        ownGame!!.status
        verify(originalGame).status
    }

    @Test
    fun forwardsCallToGetTurnNumber() {
        ownGame!!.turnNumber
        verify(originalGame).turnNumber
    }

    @Test
    @Throws(Exception::class)
    fun forwardsCallToGetPreviousTurnEvents() {
        ownGame!!.previousTurnEvents
        verify(originalGame).previousTurnEvents
    }

    @Test
    @Throws(Exception::class)
    fun returnsNullTurnEventsIfOriginalGameHasNoEventsYet() {
        doReturn(null).`when`(originalGame).previousTurnEvents
        assertThat(ownGame!!.previousTurnEvents, `is`(nullValue()))
    }

    @Test
    fun forwardsCallToEndTurn() {
        ownGame!!.endTurn()
        verify(originalGame).endTurn(self!!)
    }

    @Test
    fun forwardsCallToQuit() {
        ownGame!!.quit()
        verify(originalGame).quit(self!!)
    }

    @Test
    @Throws(Exception::class)
    fun returnsOwnPlayer() {
        assertThat(ownGame!!.me.name, `is`("Myself"))
    }

    @Test
    @Throws(Exception::class)
    fun returnsOtherPlayers() {
        assertThat(ownGame!!.otherPlayers, hasSize(1))
    }

}
