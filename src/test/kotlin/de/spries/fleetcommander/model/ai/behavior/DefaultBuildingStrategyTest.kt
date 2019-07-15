package de.spries.fleetcommander.model.ai.behavior

import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

import java.util.Arrays

import org.junit.Before
import org.junit.Test
import org.mockito.InOrder
import org.mockito.Mockito

import de.spries.fleetcommander.model.ai.behavior.BuildingStrategy
import de.spries.fleetcommander.model.ai.behavior.DefaultBuildingStrategy
import de.spries.fleetcommander.model.facade.PlayerSpecificPlanet
import de.spries.fleetcommander.model.facade.PlayerSpecificUniverse

class DefaultBuildingStrategyTest {

    private lateinit var universe: PlayerSpecificUniverse
    private lateinit var buildingStrategy: BuildingStrategy
    private lateinit var homePlanet: PlayerSpecificPlanet
    private lateinit var closePlanet: PlayerSpecificPlanet
    private lateinit var distantPlanet: PlayerSpecificPlanet

    @Before
    @Throws(Exception::class)
    fun setUp() {
        buildingStrategy = DefaultBuildingStrategy()
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

        doReturn(true).`when`(homePlanet).isInhabitedByMe
        doReturn(true).`when`(closePlanet).isInhabitedByMe
        doReturn(true).`when`(distantPlanet).isInhabitedByMe
    }

    @Test
    @Throws(Exception::class)
    fun buildsNoFactoriesWhenPlayerCannotBuild() {
        doReturn(false).`when`(homePlanet).canBuildFactory()
        buildingStrategy!!.buildFactories(universe!!)
        verify(homePlanet, never()).buildFactory()
    }

    @Test
    @Throws(Exception::class)
    fun buildsFactoriesWhenPlayerCanBuild() {
        doReturn(true).doReturn(true).doReturn(false).`when`(homePlanet).canBuildFactory()
        buildingStrategy!!.buildFactories(universe!!)
        verify(homePlanet, times(2)).buildFactory()
    }

    @Test
    fun buildsFactoriesOnPlanetsCloseToHomePlanetFirst() {
        doReturn(true).doReturn(false).`when`(homePlanet).canBuildFactory()
        doReturn(true).doReturn(false).`when`(closePlanet).canBuildFactory()
        doReturn(true).doReturn(false).`when`(distantPlanet).canBuildFactory()

        buildingStrategy!!.buildFactories(universe!!)

        val inOrder = Mockito.inOrder(homePlanet, closePlanet, distantPlanet)
        inOrder.verify(homePlanet).buildFactory()
        inOrder.verify(closePlanet).buildFactory()
        inOrder.verify(distantPlanet).buildFactory()
    }

}
