package de.spries.fleetcommander.model.facade

import org.hamcrest.Matchers.hasItem
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.nullValue
import org.junit.Assert.assertThat
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

import java.util.Arrays

import org.junit.Before
import org.junit.Test

import de.spries.fleetcommander.model.core.Player
import de.spries.fleetcommander.model.core.universe.Planet
import de.spries.fleetcommander.model.core.universe.ShipFormation
import de.spries.fleetcommander.model.core.universe.Universe

class PlayerSpecificUniverseTest {

    private lateinit var originalUniverse: Universe
    private lateinit var self: Player
    private lateinit var otherPlayer: Player
    private lateinit var ownUniverseView: PlayerSpecificUniverse
    private lateinit var otherUniverseView: PlayerSpecificUniverse
    private lateinit var myHomePlanet: Planet
    private lateinit var otherPlayersHomePlanet: Planet
    private lateinit var myShips: ShipFormation
    private lateinit var otherPlayersShips: ShipFormation

    @Before
    fun setUp() {
        originalUniverse = mock(Universe::class.java)
        self = mock(Player::class.java)
        otherPlayer = mock(Player::class.java)

        myHomePlanet = mock(Planet::class.java)
        otherPlayersHomePlanet = mock(Planet::class.java)

        doReturn(Arrays.asList(mock(Planet::class.java))).`when`(originalUniverse).planets
        doReturn(myHomePlanet).`when`(originalUniverse).getHomePlanetOf(self!!)
        doReturn(otherPlayersHomePlanet).`when`(originalUniverse).getHomePlanetOf(otherPlayer!!)

        myShips = mock(ShipFormation::class.java)
        otherPlayersShips = mock(ShipFormation::class.java)
        doReturn(self).`when`(myShips).commander
        doReturn(otherPlayer).`when`(otherPlayersShips).commander
        doReturn(Arrays.asList(myShips, otherPlayersShips)).`when`(originalUniverse).travellingShipFormations

        ownUniverseView = PlayerSpecificUniverse(originalUniverse!!, self!!)
        otherUniverseView = PlayerSpecificUniverse(originalUniverse!!, otherPlayer!!)
    }

    @Test
    fun returnsPlayerSpecificPlanets() {
        assertThat(ownUniverseView!!.planets, hasSize(1))
    }

    @Test
    fun returnsPlayerSpecificPlanet() {
        ownUniverseView!!.getPlanet(1)
        verify(originalUniverse).getPlanetForId(1)
    }

    @Test
    fun returnsPlayerSpecificHomePlanet() {
        assertThat(ownUniverseView!!.homePlanet, `is`(PlayerSpecificPlanet(myHomePlanet!!, self!!)))
        assertThat(otherUniverseView!!.homePlanet, `is`(PlayerSpecificPlanet(otherPlayersHomePlanet!!, otherPlayer!!)))
    }

    @Test
    fun returnsNullHomePlanetForDefeatedPlayers() {
        doReturn(null).`when`(originalUniverse).getHomePlanetOf(self!!)
        assertThat(ownUniverseView!!.homePlanet, `is`(nullValue()))
    }

    @Test
    fun forwardsCallToSendShipsForSelf() {
        ownUniverseView!!.sendShips(1, 2, 3)
        verify(originalUniverse).sendShips(1, 2, 3, self!!)
    }

    @Test
    fun forwardsCallToSendShipsForOtherPlayers() {
        otherUniverseView!!.sendShips(1, 2, 3)
        verify(originalUniverse).sendShips(1, 2, 3, otherPlayer!!)
    }

    @Test
    @Throws(Exception::class)
    fun returnsOnlyOwnTravellingShips() {
        assertThat(ownUniverseView!!.travellingShipFormations, hasSize(1))
        assertThat(ownUniverseView!!.travellingShipFormations, hasItem(myShips))

        assertThat(otherUniverseView!!.travellingShipFormations, hasSize(1))
        assertThat(otherUniverseView!!.travellingShipFormations, hasItem(otherPlayersShips))
    }

}
