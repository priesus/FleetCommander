package de.spries.fleetcommander.model.facade

import de.spries.fleetcommander.model.core.Player
import de.spries.fleetcommander.model.core.universe.Planet
import de.spries.fleetcommander.model.core.universe.PlanetClass
import org.junit.Before
import org.junit.Test

import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.nullValue
import org.junit.Assert.assertThat
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class PlayerSpecificPlanetTest {

    private lateinit var originalPlanet: Planet
    private lateinit var self: Player
    private lateinit var otherPlayer: Player
    private lateinit var ownPlanet: PlayerSpecificPlanet
    private lateinit var otherPlayersPlanet: PlayerSpecificPlanet

    @Before
    fun setUp() {
        originalPlanet = mock(Planet::class.java)
        self = mock(Player::class.java)
        otherPlayer = mock(Player::class.java)

        ownPlanet = PlayerSpecificPlanet(originalPlanet!!, self!!)
        otherPlayersPlanet = PlayerSpecificPlanet(originalPlanet!!, otherPlayer!!)

        doReturn(true).`when`(originalPlanet).isInhabitedBy(self!!)
        doReturn(false).`when`(originalPlanet).isInhabitedBy(otherPlayer!!)
    }

    @Test
    fun forwardsCallToGetId() {
        otherPlayersPlanet!!.id
        verify(originalPlanet).id
    }

    @Test
    fun forwardsCallToGetX() {
        otherPlayersPlanet!!.x
        verify(originalPlanet).x
    }

    @Test
    fun forwardsCallToGetY() {
        otherPlayersPlanet!!.y
        verify(originalPlanet).y
    }

    @Test
    fun forwardsCallToPlanetClassForSelf() {
        `when`(originalPlanet!!.planetClass).thenReturn(PlanetClass.P)
        ownPlanet!!.planetClass
        verify(originalPlanet).planetClass
    }

    @Test
    fun doesNotReturnPlanetClassForOtherPlayers() {
        assertThat(otherPlayersPlanet!!.planetClass, `is`("?"))
        verify(originalPlanet, never()).planetClass
    }

    @Test
    fun forwardsCallToIsHomePlanet() {
        ownPlanet!!.isMyHomePlanet
        verify(originalPlanet).isHomePlanetOf(self!!)
    }

    @Test
    fun isHomePlanetWhenOwnHomePlanet() {
        `when`(originalPlanet!!.isHomePlanetOf(self!!)).thenReturn(true)
        assertThat(ownPlanet!!.isHomePlanet, `is`(true))
    }

    @Test
    fun isNoHomePlanetWhenNotHomePlanetAtAll() {
        `when`(originalPlanet!!.isHomePlanetOf(self!!)).thenReturn(false)
        `when`(originalPlanet!!.isHomePlanet).thenReturn(false)
        assertThat(ownPlanet!!.isHomePlanet, `is`(false))
    }

    @Test
    fun isHomePlanetWhenOtherPlayersHomePlanetAndVisited() {
        `when`(originalPlanet!!.isHomePlanetOf(self!!)).thenReturn(false)
        `when`(originalPlanet!!.isHomePlanet).thenReturn(true)
        `when`(originalPlanet!!.isKnownAsEnemyPlanet(self!!)).thenReturn(true)
        assertThat(ownPlanet!!.isHomePlanet, `is`(true))
    }

    @Test
    fun isNoHomePlanetWhenOtherPlayersHomePlanetAndNotVisited() {
        `when`(originalPlanet!!.isHomePlanetOf(self!!)).thenReturn(false)
        `when`(originalPlanet!!.isHomePlanet).thenReturn(true)
        `when`(originalPlanet!!.isKnownAsEnemyPlanet(self!!)).thenReturn(false)
        assertThat(ownPlanet!!.isHomePlanet, `is`(false))
    }

    @Test
    fun forwardsCallToIsInhabitedBy() {
        ownPlanet!!.isInhabitedByMe
        verify(originalPlanet).isInhabitedBy(self!!)
    }

    @Test
    fun forwardsCallToIsKnownAsEnemyPlanet() {
        ownPlanet!!.isKnownAsEnemyPlanet
        verify(originalPlanet).isKnownAsEnemyPlanet(self!!)
    }

    @Test
    fun forwardsCallToIsUnderAttackForSelf() {
        ownPlanet!!.isUnderAttack
        verify(originalPlanet).isUnderAttack
    }

    @Test
    fun doesNotReturnIsUnderAttackForOtherPlayers() {
        assertThat(otherPlayersPlanet!!.isUnderAttack, `is`(false))
        verify(originalPlanet, never()).isUnderAttack
    }

    @Test
    fun forwardsCallToIsJustInhabitedForSelf() {
        ownPlanet!!.isJustInhabited
        verify(originalPlanet).isJustInhabited
    }

    @Test
    fun doesNotReturnIsJustInhabitedForOtherPlayers() {
        assertThat(otherPlayersPlanet!!.isJustInhabited, `is`(false))
        verify(originalPlanet, never()).isJustInhabited
    }

    @Test
    @Throws(Exception::class)
    fun forwardsCallToCanBuildFactory() {
        ownPlanet!!.canBuildFactory()
        verify(originalPlanet).canBuildFactory(self!!)
    }

    @Test
    fun forwardsCallToGetShipCountForSelf() {
        doReturn(true).`when`(originalPlanet).isInhabitedBy(self!!)

        ownPlanet!!.shipCount
        verify(originalPlanet).getShipCount()
    }

    @Test
    fun doesNotReturnShipCountForOtherPlayers() {
        assertThat(otherPlayersPlanet!!.shipCount, `is`(0))
        verify(originalPlanet, never()).getShipCount()
    }

    @Test
    fun forwardsCallToGetIncomingShips() {
        ownPlanet!!.incomingShipCount
        verify(originalPlanet).getIncomingShipCount(self!!)
    }

    @Test
    fun forwardsCallToGetFactorySiteForSelf() {
        ownPlanet!!.factorySite
        verify(originalPlanet).factorySite
    }

    @Test
    fun doesNotReturnFactorySiteForOtherPlayers() {
        assertThat(otherPlayersPlanet!!.factorySite, `is`(nullValue()))
        verify(originalPlanet, never()).factorySite
    }

    @Test
    fun forwardsCallToBuildFactory() {
        ownPlanet!!.buildFactory()
        verify(originalPlanet).buildFactory(self!!)
    }

    @Test
    fun forwardsCallToSetProductionFocus() {
        ownPlanet!!.changeProductionFocus(1)
        verify(originalPlanet).setProductionFocus(1, self!!)
    }

}