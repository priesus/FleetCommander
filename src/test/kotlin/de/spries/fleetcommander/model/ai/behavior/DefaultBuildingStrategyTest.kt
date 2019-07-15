package de.spries.fleetcommander.model.ai.behavior

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import de.spries.fleetcommander.model.facade.PlayerSpecificPlanet
import de.spries.fleetcommander.model.facade.PlayerSpecificUniverse
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

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

        doReturn(true).`when`(homePlanet).isInhabitedByMe()
        doReturn(true).`when`(closePlanet).isInhabitedByMe()
        doReturn(true).`when`(distantPlanet).isInhabitedByMe()
    }

    @Test
    @Throws(Exception::class)
    fun buildsNoFactoriesWhenPlayerCannotBuild() {
        whenever(homePlanet.canBuildFactory()).thenReturn(false)
        buildingStrategy.buildFactories(universe)
        verify(homePlanet, never()).buildFactory()
    }

    @Test
    @Throws(Exception::class)
    fun buildsFactoriesWhenPlayerCanBuild() {
        whenever(homePlanet.canBuildFactory()).thenReturn(true).thenReturn(true).thenReturn(false)
        buildingStrategy.buildFactories(universe)
        verify(homePlanet, times(2)).buildFactory()
    }

    @Test
    fun buildsFactoriesOnPlanetsCloseToHomePlanetFirst() {
        whenever(homePlanet.canBuildFactory()).thenReturn(true).thenReturn(false)
        whenever(closePlanet.canBuildFactory()).thenReturn(true).thenReturn(false)
        whenever(distantPlanet.canBuildFactory()).thenReturn(true).thenReturn(false)

        buildingStrategy.buildFactories(universe)

        val inOrder = Mockito.inOrder(homePlanet, closePlanet, distantPlanet)
        inOrder.verify(homePlanet).buildFactory()
        inOrder.verify(closePlanet).buildFactory()
        inOrder.verify(distantPlanet).buildFactory()
    }

}
