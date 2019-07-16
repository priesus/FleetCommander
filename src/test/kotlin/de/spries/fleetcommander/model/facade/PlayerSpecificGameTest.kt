package de.spries.fleetcommander.model.facade

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import de.spries.fleetcommander.model.core.Game
import de.spries.fleetcommander.model.core.Player
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.nullValue
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
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

        doReturn("Myself").`when`(self).getName()

        doReturn(listOf(self, otherPlayer)).`when`(originalGame).getPlayers()
        ownGame = PlayerSpecificGame(originalGame, self)
    }

    @Test
    fun forwardsCallToGetId() {
        ownGame.getId()
        verify(originalGame).getId()
    }

    @Test
    fun addsComputerPlayerWithName() {
        ownGame.addComputerPlayer()

        doReturn(listOf(self, otherPlayer, computerPlayer)).`when`(originalGame).getPlayers()
        ownGame.addComputerPlayer()

        argumentCaptor<Player>().apply {
            verify(originalGame, times(2)).addPlayer(capture())

            assertThat(allValues[0].getName(), `is`("Computer 1"))
            assertThat(allValues[1].getName(), `is`("Computer 2"))
        }
    }

    @Test
    fun addsHumanPlayerWithName() {
        ownGame.addHumanPlayer("Player 2")

        argumentCaptor<Player>().apply {
            verify(originalGame).addPlayer(capture())

            assertThat(firstValue.getName(), `is`("Player 2"))
        }
    }

    @Test
    fun forwardsCallToStart() {
        ownGame.start()
        verify(originalGame).start(self)
    }

    @Test
    fun forwardsCallToGetStatus() {
        ownGame.getStatus()
        verify(originalGame).getStatus()
    }

    @Test
    fun forwardsCallToGetTurnNumber() {
        ownGame.getTurnNumber()
        verify(originalGame).getTurnNumber()
    }

    @Test
    fun forwardsCallToGetPreviousTurnEvents() {
        ownGame.getPreviousTurnEvents()
        verify(originalGame).getPreviousTurnEvents()
    }

    @Test
    fun returnsNullTurnEventsIfOriginalGameHasNoEventsYet() {
        doReturn(null).`when`(originalGame).getPreviousTurnEvents()
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
    fun returnsOwnPlayer() {
        assertThat(ownGame.getMe().getName(), `is`("Myself"))
    }

    @Test
    fun returnsOtherPlayers() {
        assertThat(ownGame.getOtherPlayers(), hasSize(1))
    }

}
