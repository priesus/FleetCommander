package de.spries.fleetcommander.model.facade

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import de.spries.fleetcommander.model.core.Player
import de.spries.fleetcommander.model.core.universe.Planet
import de.spries.fleetcommander.model.core.universe.ShipFormation
import de.spries.fleetcommander.model.core.universe.Universe
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.hasItem
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.nullValue
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify

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
        originalUniverse = mock()
        self = mock()
        otherPlayer = mock()

        myHomePlanet = mock()
        otherPlayersHomePlanet = mock()

        whenever(originalUniverse.planets).thenReturn(listOf(mock()))
        whenever(originalUniverse.getHomePlanetOf(self)).thenReturn(myHomePlanet)
        whenever(originalUniverse.getHomePlanetOf(otherPlayer)).thenReturn(otherPlayersHomePlanet)

        myShips = mock()
        otherPlayersShips = mock()
        doReturn(self).`when`(myShips).commander
        doReturn(otherPlayer).`when`(otherPlayersShips).commander
        doReturn(listOf(myShips, otherPlayersShips)).`when`(originalUniverse).travellingShipFormations

        ownUniverseView = PlayerSpecificUniverse(originalUniverse, self)
        otherUniverseView = PlayerSpecificUniverse(originalUniverse, otherPlayer)
    }

    @Test
    fun returnsPlayerSpecificPlanets() {
        assertThat(ownUniverseView.getPlanets(), hasSize(1))
    }

    @Test
    fun returnsPlayerSpecificPlanet() {
        ownUniverseView.getPlanet(1)
        verify(originalUniverse).getPlanetForId(1)
    }

    @Test
    fun returnsPlayerSpecificHomePlanet() {
        assertThat(ownUniverseView.getHomePlanet(), `is`(PlayerSpecificPlanet(myHomePlanet, self)))
        assertThat(otherUniverseView.getHomePlanet(), `is`(PlayerSpecificPlanet(otherPlayersHomePlanet, otherPlayer)))
    }

    @Test
    fun returnsNullHomePlanetForDefeatedPlayers() {
        whenever(originalUniverse.getHomePlanetOf(self)).thenReturn(null)
        assertThat(ownUniverseView.getHomePlanet(), `is`(nullValue()))
    }

    @Test
    fun forwardsCallToSendShipsForSelf() {
        ownUniverseView.sendShips(1, 2, 3)
        verify(originalUniverse).sendShips(1, 2, 3, self)
    }

    @Test
    fun forwardsCallToSendShipsForOtherPlayers() {
        otherUniverseView.sendShips(1, 2, 3)
        verify(originalUniverse).sendShips(1, 2, 3, otherPlayer)
    }

    @Test
    @Throws(Exception::class)
    fun returnsOnlyOwnTravellingShips() {
        assertThat(ownUniverseView.getTravellingShipFormations(), hasSize(1))
        assertThat(ownUniverseView.getTravellingShipFormations(), hasItem(myShips))

        assertThat(otherUniverseView.getTravellingShipFormations(), hasSize(1))
        assertThat(otherUniverseView.getTravellingShipFormations(), hasItem(otherPlayersShips))
    }

}
