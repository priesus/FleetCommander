package de.spries.fleetcommander.model.core.universe

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import de.spries.fleetcommander.model.core.Player
import de.spries.fleetcommander.model.core.Player.InsufficientCreditsException
import de.spries.fleetcommander.model.core.common.IllegalActionException
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertThat
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.never
import org.mockito.Mockito.verify

class PlanetFactoryBuildingTest {
    private lateinit var jack: Player
    private lateinit var john: Player
    private lateinit var johnsHomePlanet: Planet
    private lateinit var uninhabitedPlanet: Planet
    private lateinit var johnsFactorySite: FactorySite

    @Before
    fun setUp() {
        john = mock()
        jack = mock()
        johnsFactorySite = mock()

        johnsHomePlanet = Planet(0, 0, john, johnsFactorySite)
        johnsHomePlanet.getFactorySite()
        uninhabitedPlanet = Planet(0, 0)
    }

    @Test
    fun factoryCycleInreasesNumberOfShips() {
        val shipsBefore = johnsHomePlanet.getShipCount()
        doReturn(5f).`when`(johnsFactorySite).getProducedShipsPerTurn()
        johnsHomePlanet.runProductionCycle()

        assertThat(johnsHomePlanet.getShipCount(), `is`(shipsBefore + 5))
    }

    @Test
    fun factoryCycleDoesNotInreaseNumberOfShipsAfterPlayerWasDefeated() {
        doReturn(5f).`when`(johnsFactorySite).getProducedShipsPerTurn()
        johnsHomePlanet.handleDefeatedPlayer(john)
        johnsHomePlanet.runProductionCycle()

        assertThat(johnsHomePlanet.getShipCount(), `is`(0))
    }

    @Test
    fun lowShipProductionRequiresMultipleCyclesToProduceOneShip() {
        val shipsBefore = johnsHomePlanet.getShipCount()
        doReturn(0.35f).`when`(johnsFactorySite).getProducedShipsPerTurn()

        johnsHomePlanet.runProductionCycle()
        assertThat(johnsHomePlanet.getShipCount(), `is`(shipsBefore))

        johnsHomePlanet.runProductionCycle()
        assertThat(johnsHomePlanet.getShipCount(), `is`(shipsBefore))

        johnsHomePlanet.runProductionCycle()
        assertThat(johnsHomePlanet.getShipCount(), `is`(shipsBefore + 1))
    }

    @Test(expected = IllegalActionException::class)
    fun buildFactoryOnUninhabitedPlanetThrowsException() {
        uninhabitedPlanet.buildFactory(john)
    }

    @Test(expected = IllegalActionException::class)
    fun buildFactoryOnOtherPlayersPlanetThrowsException() {
        johnsHomePlanet.buildFactory(jack)
    }

    @Test
    fun buildingFactoryReducesPlayerCredits() {
        doReturn(SUFFICIENT_CREDITS).`when`(john).getCredits()
        whenever(johnsFactorySite.hasAvailableSlots()).thenReturn(true)

        johnsHomePlanet.buildFactory(john)
        verify(john).reduceCredits(eq(SUFFICIENT_CREDITS))
    }

    @Test
    fun noCreditsRemovedWhenNoFactorySlotsAvailable() {
        whenever(johnsFactorySite.hasAvailableSlots()).thenReturn(false)
        whenever(johnsFactorySite.buildFactory()).thenThrow(IllegalActionException("error"))

        try {
            johnsHomePlanet.buildFactory(john)
            fail("Expected exception")
        } catch (e: IllegalActionException) {
            // expected behavior
        }

        verify(john, never()).reduceCredits(any())
    }

    @Test
    fun cannotBuildFactoryWithInsufficientCredits_() {
        whenever(johnsFactorySite.hasAvailableSlots()).thenReturn(true)
        whenever(john.reduceCredits(any())).thenThrow(InsufficientCreditsException("error"))

        try {
            johnsHomePlanet.buildFactory(john)
            fail("Expected exception")
        } catch (e: IllegalActionException) {
            // expected behavior
        }

        verify(johnsFactorySite, never()).buildFactory()
    }

    @Test
    fun cannotBuildFactoryWithInsufficientCredits() {
        doReturn(INSUFFICIENT_CREDITS).`when`(john).getCredits()
        whenever(johnsFactorySite.hasAvailableSlots()).thenReturn(true)

        assertThat(johnsHomePlanet.canBuildFactory(john), `is`(false))
        verify(john).getCredits()
    }

    @Test
    fun cannotBuildFactoryWithoutAvailableSlots() {
        doReturn(SUFFICIENT_CREDITS).`when`(john).getCredits()
        whenever(johnsFactorySite.hasAvailableSlots()).thenReturn(false)

        assertThat(johnsHomePlanet.canBuildFactory(john), `is`(false))
        verify(johnsFactorySite).hasAvailableSlots()
    }

    @Test
    fun cannotBuildFactoryOnOtherPlayersPlanet() {
        doReturn(SUFFICIENT_CREDITS).`when`(john).getCredits()
        whenever(johnsFactorySite.hasAvailableSlots()).thenReturn(true)
        assertThat(johnsHomePlanet.canBuildFactory(jack), `is`(false))
    }

    @Test
    fun cannotBuildFactoryOnUninhabitedPlanet() {
        doReturn(SUFFICIENT_CREDITS).`when`(john).getCredits()
        whenever(johnsFactorySite.hasAvailableSlots()).thenReturn(true)
        assertThat(uninhabitedPlanet.canBuildFactory(john), `is`(false))
    }

    @Test
    fun canBuildFactory() {
        doReturn(SUFFICIENT_CREDITS).`when`(john).getCredits()
        whenever(johnsFactorySite.hasAvailableSlots()).thenReturn(true)
        assertThat(johnsHomePlanet.canBuildFactory(john), `is`(true))
    }

    @Test
    fun canSetProductionFocusIfInhabitant() {
        johnsHomePlanet.setProductionFocus(1, john)
        verify(johnsFactorySite).updateShipProductionFocus(1)
    }

    @Test(expected = IllegalActionException::class)
    fun cannotSetProductionFocusIfNotInhabitant() {
        johnsHomePlanet.setProductionFocus(1, jack)
    }

    companion object {

        private val SUFFICIENT_CREDITS = FactorySite.FACTORY_COST
        private val INSUFFICIENT_CREDITS = SUFFICIENT_CREDITS - 1
    }
}
