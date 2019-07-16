package de.spries.fleetcommander.model.ai.behavior

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import de.spries.fleetcommander.model.facade.PlayerSpecificPlanet
import de.spries.fleetcommander.model.facade.PlayerSpecificUniverse
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify

class AggressiveFleetStrategyTest {

    private lateinit var universe: PlayerSpecificUniverse
    private lateinit var fleetStrategy: FleetStrategy
    private lateinit var homePlanet: PlayerSpecificPlanet
    private lateinit var closePlanet: PlayerSpecificPlanet
    private lateinit var distantPlanet: PlayerSpecificPlanet

    @Before
    fun setUp() {
        fleetStrategy = AggressiveFleetStrategy()
        universe = mock()
        homePlanet = mock()
        closePlanet = mock()
        distantPlanet = mock()

        doReturn(homePlanet).`when`(universe).getHomePlanet()
        val planets = listOf(closePlanet, distantPlanet, homePlanet)
        doReturn(planets).`when`(universe).getPlanets()

        whenever(homePlanet.distanceTo(homePlanet)).thenReturn(0.0)
        whenever(closePlanet.distanceTo(homePlanet)).thenReturn(1.0)
        whenever(distantPlanet.distanceTo(homePlanet)).thenReturn(2.0)

        doReturn(1).`when`(homePlanet).getId()
        doReturn(2).`when`(closePlanet).getId()
        doReturn(3).`when`(distantPlanet).getId()

        doReturn(true).`when`(homePlanet).isInhabitedByMe()
        doReturn(false).`when`(homePlanet).isKnownAsEnemyPlanet()
    }

    @Test
    fun sendsAvailableShipToClosestPlanets() {
        doReturn(1).`when`(homePlanet).getShipCount()
        doReturn(false).`when`(closePlanet).isInhabitedByMe()
        doReturn(false).`when`(distantPlanet).isInhabitedByMe()
        doReturn(false).`when`(closePlanet).isKnownAsEnemyPlanet()
        doReturn(false).`when`(distantPlanet).isKnownAsEnemyPlanet()
        doReturn(0).`when`(closePlanet).getIncomingShipCount()
        doReturn(0).`when`(distantPlanet).getIncomingShipCount()

        fleetStrategy.sendShips(universe)

        verify(universe).sendShips(1, 1, 2)
    }

    @Test
    fun sendsAvailableShipToClosestUninhabitedPlanets() {
        doReturn(1).`when`(homePlanet).getShipCount()
        doReturn(true).`when`(closePlanet).isInhabitedByMe()
        doReturn(false).`when`(distantPlanet).isInhabitedByMe()
        doReturn(false).`when`(closePlanet).isKnownAsEnemyPlanet()
        doReturn(false).`when`(distantPlanet).isKnownAsEnemyPlanet()
        doReturn(0).`when`(closePlanet).getIncomingShipCount()
        doReturn(0).`when`(distantPlanet).getIncomingShipCount()

        fleetStrategy.sendShips(universe)

        verify(universe).sendShips(1, 1, 3)
    }

    @Test
    fun sendsOnlyOneShipToUninhabitedPlanets() {
        doReturn(2).`when`(homePlanet).getShipCount()
        doReturn(false).`when`(closePlanet).isInhabitedByMe()
        doReturn(false).`when`(distantPlanet).isInhabitedByMe()
        doReturn(false).`when`(closePlanet).isKnownAsEnemyPlanet()
        doReturn(false).`when`(distantPlanet).isKnownAsEnemyPlanet()
        doReturn(0).`when`(closePlanet).getIncomingShipCount()
        doReturn(0).`when`(distantPlanet).getIncomingShipCount()

        fleetStrategy.sendShips(universe)

        verify(universe).sendShips(1, 1, 2)
        verify(universe).sendShips(1, 1, 3)
    }

    @Test
    fun sendsOnlyOneShipToPlanetsWithoutIncomingShips() {
        doReturn(2).`when`(homePlanet).getShipCount()
        doReturn(false).`when`(closePlanet).isInhabitedByMe()
        doReturn(false).`when`(distantPlanet).isInhabitedByMe()
        doReturn(false).`when`(closePlanet).isKnownAsEnemyPlanet()
        doReturn(false).`when`(distantPlanet).isKnownAsEnemyPlanet()
        doReturn(1).`when`(closePlanet).getIncomingShipCount()
        doReturn(0).`when`(distantPlanet).getIncomingShipCount()

        fleetStrategy.sendShips(universe)

        verify(universe).sendShips(1, 1, 3)
    }

    @Test
    fun sendsAllShipsToEnemyPlanetIfOneIsKnown() {
        doReturn(2).`when`(homePlanet).getShipCount()
        doReturn(3).`when`(closePlanet).getShipCount()
        doReturn(true).`when`(closePlanet).isInhabitedByMe()
        doReturn(false).`when`(distantPlanet).isInhabitedByMe()
        doReturn(false).`when`(closePlanet).isKnownAsEnemyPlanet()
        doReturn(true).`when`(distantPlanet).isKnownAsEnemyPlanet()

        fleetStrategy.sendShips(universe)

        verify(universe).sendShips(2, 1, 3)
        verify(universe).sendShips(3, 2, 3)
    }

}
