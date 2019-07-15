package de.spries.fleetcommander.model.core.universe

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import de.spries.fleetcommander.model.core.Player
import de.spries.fleetcommander.model.core.Player.InsufficientCreditsException
import de.spries.fleetcommander.model.core.common.IllegalActionException
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertThat
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
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

        johnsHomePlanet = Planet(0, 0, john)
        uninhabitedPlanet = Planet(0, 0)
    }

    @Test
    @Throws(Exception::class)
    fun factoryCycleInreasesNumberOfShips() {
        val shipsBefore = johnsHomePlanet.getShipCount()
        doReturn(5f).`when`(johnsFactorySite).getProducedShipsPerTurn()
        johnsHomePlanet.runProductionCycle()

        assertThat(johnsHomePlanet.getShipCount(), `is`(shipsBefore + 5))
    }

    @Test
    @Throws(Exception::class)
    fun factoryCycleDoesNotInreaseNumberOfShipsAfterPlayerWasDefeated() {
        doReturn(5f).`when`(johnsFactorySite).getProducedShipsPerTurn()
        johnsHomePlanet.handleDefeatedPlayer(john)
        johnsHomePlanet.runProductionCycle()

        assertThat(johnsHomePlanet.getShipCount(), `is`(0))
    }

    @Test
    @Throws(Exception::class)
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
    @Throws(Exception::class)
    fun buildFactoryOnUninhabitedPlanetThrowsException() {
        uninhabitedPlanet.buildFactory(john)
    }

    @Test(expected = IllegalActionException::class)
    @Throws(Exception::class)
    fun buildFactoryOnOtherPlayersPlanetThrowsException() {
        johnsHomePlanet.buildFactory(jack)
    }

    @Test
    @Throws(Exception::class)
    fun buildingFactoryReducesPlayerCredits() {
        doReturn(SUFFICIENT_CREDITS).`when`(john).credits
        whenever(johnsFactorySite.hasAvailableSlots()).thenReturn(true)

        johnsHomePlanet.buildFactory(john)
        verify(john).reduceCredits(Mockito.eq(SUFFICIENT_CREDITS))
    }

    @Test
    @Throws(Exception::class)
    fun noCreditsRemovedWhenNoFactorySlotsAvailable() {
        whenever(johnsFactorySite.hasAvailableSlots()).thenReturn(false)
        whenever(johnsFactorySite.buildFactory()).thenThrow(IllegalActionException("error"))

        try {
            johnsHomePlanet.buildFactory(john)
            fail("Expected exception")
        } catch (e: IllegalActionException) {
            // expected behavior
        }

        verify(john, never()).reduceCredits(Mockito.anyInt())
    }

    @Test
    @Throws(Exception::class)
    fun cannotBuildFactoryWithInsufficientCredits_() {
        whenever(johnsFactorySite.hasAvailableSlots()).thenReturn(true)
        whenever(john.reduceCredits(Mockito.anyInt())).thenThrow(InsufficientCreditsException("error"))

        try {
            johnsHomePlanet.buildFactory(john)
            fail("Expected exception")
        } catch (e: IllegalActionException) {
            // expected behavior
        }

        verify(johnsFactorySite, never()).buildFactory()
    }

    @Test
    @Throws(Exception::class)
    fun cannotBuildFactoryWithInsufficientCredits() {
        doReturn(INSUFFICIENT_CREDITS).`when`(john).credits
        whenever(johnsFactorySite.hasAvailableSlots()).thenReturn(true)

        assertThat(johnsHomePlanet.canBuildFactory(john), `is`(false))
        verify(john).credits
    }

    @Test
    @Throws(Exception::class)
    fun cannotBuildFactoryWithoutAvailableSlots() {
        doReturn(SUFFICIENT_CREDITS).`when`(john).credits
        whenever(johnsFactorySite.hasAvailableSlots()).thenReturn(false)

        assertThat(johnsHomePlanet.canBuildFactory(john), `is`(false))
        verify(johnsFactorySite).hasAvailableSlots()
    }

    @Test
    @Throws(Exception::class)
    fun cannotBuildFactoryOnOtherPlayersPlanet() {
        doReturn(SUFFICIENT_CREDITS).`when`(john).credits
        whenever(johnsFactorySite.hasAvailableSlots()).thenReturn(true)
        assertThat(johnsHomePlanet.canBuildFactory(jack), `is`(false))
    }

    @Test
    @Throws(Exception::class)
    fun cannotBuildFactoryOnUninhabitedPlanet() {
        doReturn(SUFFICIENT_CREDITS).`when`(john).credits
        whenever(johnsFactorySite.hasAvailableSlots()).thenReturn(true)
        assertThat(uninhabitedPlanet.canBuildFactory(john), `is`(false))
    }

    @Test
    @Throws(Exception::class)
    fun canBuildFactory() {
        doReturn(SUFFICIENT_CREDITS).`when`(john).credits
        whenever(johnsFactorySite.hasAvailableSlots()).thenReturn(true)
        assertThat(johnsHomePlanet.canBuildFactory(john), `is`(true))
    }

    @Test
    @Throws(Exception::class)
    fun canSetProductionFocusIfInhabitant() {
        johnsHomePlanet.setProductionFocus(1, john)
        verify(johnsFactorySite).shipProductionFocus = 1
    }

    @Test(expected = IllegalActionException::class)
    @Throws(Exception::class)
    fun cannotSetProductionFocusIfNotInhabitant() {
        johnsHomePlanet.setProductionFocus(1, jack)
    }

    companion object {

        private val SUFFICIENT_CREDITS = FactorySite.FACTORY_COST
        private val INSUFFICIENT_CREDITS = SUFFICIENT_CREDITS - 1
    }
}
