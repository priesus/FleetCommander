package de.spries.fleetcommander.model.facade

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import de.spries.fleetcommander.model.core.Player
import de.spries.fleetcommander.model.core.universe.Planet
import de.spries.fleetcommander.model.core.universe.PlanetClass
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.nullValue
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.never
import org.mockito.Mockito.verify

class PlayerSpecificPlanetTest {

    private lateinit var originalPlanet: Planet
    private lateinit var self: Player
    private lateinit var otherPlayer: Player
    private lateinit var ownPlanet: PlayerSpecificPlanet
    private lateinit var otherPlayersPlanet: PlayerSpecificPlanet

    @Before
    fun setUp() {
        originalPlanet = mock()
        self = mock()
        otherPlayer = mock()

        ownPlanet = PlayerSpecificPlanet(originalPlanet, self)
        otherPlayersPlanet = PlayerSpecificPlanet(originalPlanet, otherPlayer)

        whenever(originalPlanet.isInhabitedBy(self)).thenReturn(true)
        whenever(originalPlanet.isInhabitedBy(otherPlayer)).thenReturn(false)
    }

    @Test
    fun forwardsCallToGetId() {
        otherPlayersPlanet.getId()
        verify(originalPlanet).id
    }

    @Test
    fun forwardsCallToPlanetClassForSelf() {
        `when`(originalPlanet.getPlanetClass()).thenReturn(PlanetClass.P)
        ownPlanet.getPlanetClass()
        verify(originalPlanet).getPlanetClass()
    }

    @Test
    fun doesNotReturnPlanetClassForOtherPlayers() {
        assertThat(otherPlayersPlanet.getPlanetClass(), `is`("?"))
        verify(originalPlanet, never()).getPlanetClass()
    }

    @Test
    fun forwardsCallToIsHomePlanet() {
        ownPlanet.isMyHomePlanet()
        verify(originalPlanet).isHomePlanetOf(self)
    }

    @Test
    fun isHomePlanetWhenOwnHomePlanet() {
        `when`(originalPlanet.isHomePlanetOf(self)).thenReturn(true)
        assertThat(ownPlanet.isHomePlanet(), `is`(true))
    }

    @Test
    fun isNoHomePlanetWhenNotHomePlanetAtAll() {
        `when`(originalPlanet.isHomePlanetOf(self)).thenReturn(false)
        `when`(originalPlanet.isHomePlanet()).thenReturn(false)
        assertThat(ownPlanet.isHomePlanet(), `is`(false))
    }

    @Test
    fun isHomePlanetWhenOtherPlayersHomePlanetAndVisited() {
        `when`(originalPlanet.isHomePlanetOf(self)).thenReturn(false)
        `when`(originalPlanet.isHomePlanet()).thenReturn(true)
        `when`(originalPlanet.isKnownAsEnemyPlanet(self)).thenReturn(true)
        assertThat(ownPlanet.isHomePlanet(), `is`(true))
    }

    @Test
    fun isNoHomePlanetWhenOtherPlayersHomePlanetAndNotVisited() {
        `when`(originalPlanet.isHomePlanetOf(self)).thenReturn(false)
        `when`(originalPlanet.isHomePlanet()).thenReturn(true)
        `when`(originalPlanet.isKnownAsEnemyPlanet(self)).thenReturn(false)
        assertThat(ownPlanet.isHomePlanet(), `is`(false))
    }

    @Test
    fun forwardsCallToIsInhabitedBy() {
        ownPlanet.isInhabitedByMe()
        verify(originalPlanet).isInhabitedBy(self)
    }

    @Test
    fun forwardsCallToIsKnownAsEnemyPlanet() {
        ownPlanet.isKnownAsEnemyPlanet()
        verify(originalPlanet).isKnownAsEnemyPlanet(self)
    }

    @Test
    fun forwardsCallToIsUnderAttackForSelf() {
        ownPlanet.isUnderAttack()
        verify(originalPlanet).isUnderAttack()
    }

    @Test
    fun doesNotReturnIsUnderAttackForOtherPlayers() {
        assertThat(otherPlayersPlanet.isUnderAttack(), `is`(false))
        verify(originalPlanet, never()).isUnderAttack()
    }

    @Test
    fun forwardsCallToIsJustInhabitedForSelf() {
        ownPlanet.isJustInhabited()
        verify(originalPlanet).isJustInhabited()
    }

    @Test
    fun doesNotReturnIsJustInhabitedForOtherPlayers() {
        assertThat(otherPlayersPlanet.isJustInhabited(), `is`(false))
        verify(originalPlanet, never()).isJustInhabited()
    }

    @Test
    fun forwardsCallToCanBuildFactory() {
        ownPlanet.canBuildFactory()
        verify(originalPlanet).canBuildFactory(self)
    }

    @Test
    fun forwardsCallToGetShipCountForSelf() {
        whenever(originalPlanet.isInhabitedBy(self)).thenReturn(true)

        ownPlanet.getShipCount()
        verify(originalPlanet).getShipCount()
    }

    @Test
    fun doesNotReturnShipCountForOtherPlayers() {
        assertThat(otherPlayersPlanet.getShipCount(), `is`(0))
        verify(originalPlanet, never()).getShipCount()
    }

    @Test
    fun forwardsCallToGetIncomingShips() {
        ownPlanet.getIncomingShipCount()
        verify(originalPlanet).getIncomingShipCount(self)
    }

    @Test
    fun forwardsCallToGetFactorySiteForSelf() {
        ownPlanet.getFactorySite()
        verify(originalPlanet).getFactorySite()
    }

    @Test
    fun doesNotReturnFactorySiteForOtherPlayers() {
        assertThat(otherPlayersPlanet.getFactorySite(), `is`(nullValue()))
        verify(originalPlanet, never()).getFactorySite()
    }

    @Test
    fun forwardsCallToBuildFactory() {
        ownPlanet.buildFactory()
        verify(originalPlanet).buildFactory(self)
    }

    @Test
    fun forwardsCallToSetProductionFocus() {
        ownPlanet.changeProductionFocus(1)
        verify(originalPlanet).setProductionFocus(1, self)
    }

}
