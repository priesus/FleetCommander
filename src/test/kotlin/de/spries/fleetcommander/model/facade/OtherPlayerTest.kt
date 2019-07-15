package de.spries.fleetcommander.model.facade

import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

import org.junit.Before
import org.junit.Test

import de.spries.fleetcommander.model.core.Player

class OtherPlayerTest {

    private lateinit var originalPlayer: Player
    private lateinit var viewingPlayer: OtherPlayer

    @Before
    fun setUp() {
        originalPlayer = mock(Player::class.java)
        viewingPlayer = OtherPlayer(originalPlayer!!)
    }

    @Test
    fun forwardsCallToGetNameForOtherPlayers() {
        viewingPlayer!!.name
        verify(originalPlayer).name
    }

    @Test
    fun forwardsCallToGetStatus() {
        viewingPlayer!!.status
        verify(originalPlayer).status
    }

    @Test
    fun forwardsCallToIsHumanPlayer() {
        viewingPlayer!!.isHumanPlayer
        verify(originalPlayer).isHumanPlayer()
    }
}
