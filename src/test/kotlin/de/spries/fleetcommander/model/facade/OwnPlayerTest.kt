package de.spries.fleetcommander.model.facade

import com.nhaarman.mockito_kotlin.mock
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
        verify(originalPlayer).credits
    }

    @Test
    @Throws(Exception::class)
    fun forwardsCallToCanAffordFactory() {
        viewingPlayer.canAffordFactory()
        verify(originalPlayer).canAffordFactory()
    }
}
