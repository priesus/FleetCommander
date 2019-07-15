package de.spries.fleetcommander.model.facade

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import de.spries.fleetcommander.model.core.Game
import de.spries.fleetcommander.model.core.Player
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.nullValue
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

class PlayerSpecificGameTest {

    private lateinit var originalGame: Game
    private lateinit var self: Player
    private lateinit var otherPlayer: Player
    private lateinit var computerPlayer: Player
    private lateinit var ownGame: PlayerSpecificGame

    @Before
    fun setUp() {
        originalGame = mock()
        self = mock()
        otherPlayer = mock()
        computerPlayer = mock()

        whenever(self.isHumanPlayer()).thenReturn(true)
        whenever(otherPlayer.isHumanPlayer()).thenReturn(true)
        whenever(computerPlayer.isHumanPlayer()).thenReturn(false)

        doReturn("Myself").`when`(self).name

        doReturn(listOf(self, otherPlayer)).`when`(originalGame).players
        ownGame = PlayerSpecificGame(originalGame, self)
    }

    @Test
    fun forwardsCallToGetId() {
        ownGame.getId()
        verify(originalGame).id
    }

    @Test
    @Throws(Exception::class)
    fun addsComputerPlayerWithName() {
        ownGame.addComputerPlayer()

        doReturn(listOf(self, otherPlayer, computerPlayer)).`when`(originalGame).players
        ownGame.addComputerPlayer()

        val argument = ArgumentCaptor.forClass(Player::class.java)
        verify(originalGame, times(2)).addPlayer(argument.capture())
        assertThat(argument.allValues[0].name, `is`("Computer 1"))
        assertThat(argument.allValues[1].name, `is`("Computer 2"))
    }

    @Test
    @Throws(Exception::class)
    fun addsHumanPlayerWithName() {
        ownGame.addHumanPlayer("Player 2")

        val argument = ArgumentCaptor.forClass(Player::class.java)
        verify(originalGame).addPlayer(argument.capture())
        assertThat(argument.value.name, `is`("Player 2"))
    }

    @Test
    fun forwardsCallToStart() {
        ownGame.start()
        verify(originalGame).start(self)
    }

    @Test
    fun forwardsCallToGetStatus() {
        ownGame.getStatus()
        verify(originalGame).status
    }

    @Test
    fun forwardsCallToGetTurnNumber() {
        ownGame.getTurnNumber()
        verify(originalGame).turnNumber
    }

    @Test
    @Throws(Exception::class)
    fun forwardsCallToGetPreviousTurnEvents() {
        ownGame.getPreviousTurnEvents()
        verify(originalGame).previousTurnEvents
    }

    @Test
    @Throws(Exception::class)
    fun returnsNullTurnEventsIfOriginalGameHasNoEventsYet() {
        doReturn(null).`when`(originalGame).previousTurnEvents
        assertThat(ownGame.getPreviousTurnEvents(), `is`(nullValue()))
    }

    @Test
    fun forwardsCallToEndTurn() {
        ownGame.endTurn()
        verify(originalGame).endTurn(self)
    }

    @Test
    fun forwardsCallToQuit() {
        ownGame.quit()
        verify(originalGame).quit(self)
    }

    @Test
    @Throws(Exception::class)
    fun returnsOwnPlayer() {
        assertThat(ownGame.getMe().getName(), `is`("Myself"))
    }

    @Test
    @Throws(Exception::class)
    fun returnsOtherPlayers() {
        assertThat(ownGame.getOtherPlayers(), hasSize(1))
    }

}
