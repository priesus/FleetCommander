package de.spries.fleetcommander.model.facade

import com.nhaarman.mockitokotlin2.mock
import de.spries.fleetcommander.model.core.Player
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.verify

class OtherPlayerTest {

    private lateinit var originalPlayer: Player
    private lateinit var viewingPlayer: OtherPlayer

    @Before
    fun setUp() {
        originalPlayer = mock()
        viewingPlayer = OtherPlayer(originalPlayer)
    }

    @Test
    fun forwardsCallToGetNameForOtherPlayers() {
        viewingPlayer.getName()
        verify(originalPlayer).name
    }

    @Test
    fun forwardsCallToGetStatus() {
        viewingPlayer.getStatus()
        verify(originalPlayer).status
    }

    @Test
    fun forwardsCallToIsHumanPlayer() {
        viewingPlayer.isHumanPlayer()
        verify(originalPlayer).isHumanPlayer()
    }
}
