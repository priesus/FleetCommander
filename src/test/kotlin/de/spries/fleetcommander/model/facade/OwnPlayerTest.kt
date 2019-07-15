package de.spries.fleetcommander.model.facade

import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

import org.junit.Before
import org.junit.Test

import de.spries.fleetcommander.model.core.Player

class OwnPlayerTest {

    private lateinit var originalPlayer: Player
    private lateinit var viewingPlayer: OwnPlayer

    @Before
    fun setUp() {
        originalPlayer = mock(Player::class.java)
        viewingPlayer = OwnPlayer(originalPlayer!!)
    }

    @Test
    fun forwardsCallToGetNameForOtherPlayers() {
        viewingPlayer!!.credits
        verify(originalPlayer).credits
    }

    @Test
    @Throws(Exception::class)
    fun forwardsCallToCanAffordFactory() {
        viewingPlayer!!.canAffordFactory
        verify(originalPlayer).canAffordFactory()
    }
}
