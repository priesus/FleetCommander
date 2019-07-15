package de.spries.fleetcommander.model.ai.behavior

import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

import java.util.Arrays

import org.junit.Before
import org.junit.Test

import de.spries.fleetcommander.model.ai.behavior.AggressiveFleetStrategy
import de.spries.fleetcommander.model.ai.behavior.FleetStrategy
import de.spries.fleetcommander.model.facade.PlayerSpecificPlanet
import de.spries.fleetcommander.model.facade.PlayerSpecificUniverse

class AggressiveFleetStrategyTest {

    private lateinit var universe: PlayerSpecificUniverse
    private lateinit var fleetStrategy: FleetStrategy
    private lateinit var homePlanet: PlayerSpecificPlanet
    private lateinit var closePlanet: PlayerSpecificPlanet
    private lateinit var distantPlanet: PlayerSpecificPlanet

    @Before
    @Throws(Exception::class)
    fun setUp() {
        fleetStrategy = AggressiveFleetStrategy()
        universe = mock(PlayerSpecificUniverse::class.java)
        homePlanet = mock(PlayerSpecificPlanet::class.java)
        closePlanet = mock(PlayerSpecificPlanet::class.java)
        distantPlanet = mock(PlayerSpecificPlanet::class.java)

        doReturn(homePlanet).`when`(universe).homePlanet
        val planets = Arrays.asList(closePlanet, distantPlanet, homePlanet)
        doReturn(planets).`when`(universe).planets

        doReturn(0.0).`when`(homePlanet).distanceTo(homePlanet!!)
        doReturn(1.0).`when`(closePlanet).distanceTo(homePlanet!!)
        doReturn(2.0).`when`(distantPlanet).distanceTo(homePlanet!!)

        doReturn(1).`when`(homePlanet).id
        doReturn(2).`when`(closePlanet).id
        doReturn(3).`when`(distantPlanet).id

        doReturn(true).`when`(homePlanet).isInhabitedByMe
        doReturn(false).`when`(homePlanet).isKnownAsEnemyPlanet
    }

    @Test
    fun sendsAvailableShipToClosestPlanets() {
        doReturn(1).`when`(homePlanet).shipCount
        doReturn(false).`when`(closePlanet).isInhabitedByMe
        doReturn(false).`when`(distantPlanet).isInhabitedByMe
        doReturn(false).`when`(closePlanet).isKnownAsEnemyPlanet
        doReturn(false).`when`(distantPlanet).isKnownAsEnemyPlanet
        doReturn(0).`when`(closePlanet).incomingShipCount
        doReturn(0).`when`(distantPlanet).incomingShipCount

        fleetStrategy!!.sendShips(universe!!)

        verify(universe).sendShips(1, 1, 2)
    }

    @Test
    fun sendsAvailableShipToClosestUninhabitedPlanets() {
        doReturn(1).`when`(homePlanet).shipCount
        doReturn(true).`when`(closePlanet).isInhabitedByMe
        doReturn(false).`when`(distantPlanet).isInhabitedByMe
        doReturn(false).`when`(closePlanet).isKnownAsEnemyPlanet
        doReturn(false).`when`(distantPlanet).isKnownAsEnemyPlanet
        doReturn(0).`when`(closePlanet).incomingShipCount
        doReturn(0).`when`(distantPlanet).incomingShipCount

        fleetStrategy!!.sendShips(universe!!)

        verify(universe).sendShips(1, 1, 3)
    }

    @Test
    fun sendsOnlyOneShipToUninhabitedPlanets() {
        doReturn(2).`when`(homePlanet).shipCount
        doReturn(false).`when`(closePlanet).isInhabitedByMe
        doReturn(false).`when`(distantPlanet).isInhabitedByMe
        doReturn(false).`when`(closePlanet).isKnownAsEnemyPlanet
        doReturn(false).`when`(distantPlanet).isKnownAsEnemyPlanet
        doReturn(0).`when`(closePlanet).incomingShipCount
        doReturn(0).`when`(distantPlanet).incomingShipCount

        fleetStrategy!!.sendShips(universe!!)

        verify(universe).sendShips(1, 1, 2)
        verify(universe).sendShips(1, 1, 3)
    }

    @Test
    fun sendsOnlyOneShipToPlanetsWithoutIncomingShips() {
        doReturn(2).`when`(homePlanet).shipCount
        doReturn(false).`when`(closePlanet).isInhabitedByMe
        doReturn(false).`when`(distantPlanet).isInhabitedByMe
        doReturn(false).`when`(closePlanet).isKnownAsEnemyPlanet
        doReturn(false).`when`(distantPlanet).isKnownAsEnemyPlanet
        doReturn(1).`when`(closePlanet).incomingShipCount
        doReturn(0).`when`(distantPlanet).incomingShipCount

        fleetStrategy!!.sendShips(universe!!)

        verify(universe).sendShips(1, 1, 3)
    }

    @Test
    @Throws(Exception::class)
    fun sendsAllShipsToEnemyPlanetIfOneIsKnown() {
        doReturn(2).`when`(homePlanet).shipCount
        doReturn(3).`when`(closePlanet).shipCount
        doReturn(true).`when`(closePlanet).isInhabitedByMe
        doReturn(false).`when`(distantPlanet).isInhabitedByMe
        doReturn(false).`when`(closePlanet).isKnownAsEnemyPlanet
        doReturn(true).`when`(distantPlanet).isKnownAsEnemyPlanet

        fleetStrategy!!.sendShips(universe!!)

        verify(universe).sendShips(2, 1, 3)
        verify(universe).sendShips(3, 2, 3)
    }

}
