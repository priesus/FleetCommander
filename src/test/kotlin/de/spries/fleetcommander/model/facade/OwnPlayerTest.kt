package de.spries.fleetcommander.model.facade

import com.nhaarman.mockitokotlin2.mock
import de.spries.fleetcommander.model.core.Player
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.verify

class OwnPlayerTest {

    private lateinit var originalPlayer: Player
    private lateinit var viewingPlayer: OwnPlayer

    @Before
    fun setUp() {
        originalPlayer = mock()
        viewingPlayer = OwnPlayer(originalPlayer)
    }

    @Test
    fun forwardsCallToGetNameForOtherPlayers() {
        viewingPlayer.getCredits()
        verify(originalPlayer).getCredits()
    }

    @Test
    @Throws(Exception::class)
    fun forwardsCallToCanAffordFactory() {
        viewingPlayer.getCanAffordFactory()
        verify(originalPlayer).canAffordFactory()
    }
}
