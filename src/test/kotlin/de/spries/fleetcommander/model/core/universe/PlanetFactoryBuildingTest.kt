package de.spries.fleetcommander.model.core.universe

import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertThat
import org.junit.Assert.fail
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify

import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

import de.spries.fleetcommander.model.core.Player
import de.spries.fleetcommander.model.core.Player.InsufficientCreditsException
import de.spries.fleetcommander.model.core.common.IllegalActionException

class PlanetFactoryBuildingTest {
    private lateinit var jack: Player
    private lateinit var john: Player
    private lateinit var johnsHomePlanet: Planet
    private lateinit var uninhabitedPlanet: Planet
    private lateinit var johnsFactorySite: FactorySite

    @Before
    fun setUp() {
        john = mock(Player::class.java)
        jack = mock(Player::class.java)

        johnsHomePlanet = Planet(0, 0, john!!)
        uninhabitedPlanet = Planet(0, 0)
    }

    @Test
    @Throws(Exception::class)
    fun factoryCycleInreasesNumberOfShips() {
        val shipsBefore = johnsHomePlanet!!.getShipCount()
        doReturn(5f).`when`(johnsFactorySite).producedShipsPerTurn
        johnsHomePlanet!!.runProductionCycle()

        assertThat(johnsHomePlanet!!.getShipCount(), `is`(shipsBefore + 5))
    }

    @Test
    @Throws(Exception::class)
    fun factoryCycleDoesNotInreaseNumberOfShipsAfterPlayerWasDefeated() {
        doReturn(5f).`when`(johnsFactorySite).producedShipsPerTurn
        johnsHomePlanet!!.handleDefeatedPlayer(john!!)
        johnsHomePlanet!!.runProductionCycle()

        assertThat(johnsHomePlanet!!.getShipCount(), `is`(0))
    }

    @Test
    @Throws(Exception::class)
    fun lowShipProductionRequiresMultipleCyclesToProduceOneShip() {
        val shipsBefore = johnsHomePlanet!!.getShipCount()
        doReturn(0.35f).`when`(johnsFactorySite).producedShipsPerTurn

        johnsHomePlanet!!.runProductionCycle()
        assertThat(johnsHomePlanet!!.getShipCount(), `is`(shipsBefore))

        johnsHomePlanet!!.runProductionCycle()
        assertThat(johnsHomePlanet!!.getShipCount(), `is`(shipsBefore))

        johnsHomePlanet!!.runProductionCycle()
        assertThat(johnsHomePlanet!!.getShipCount(), `is`(shipsBefore + 1))
    }

    @Test(expected = IllegalActionException::class)
    @Throws(Exception::class)
    fun buildFactoryOnUninhabitedPlanetThrowsException() {
        uninhabitedPlanet!!.buildFactory(john!!)
    }

    @Test(expected = IllegalActionException::class)
    @Throws(Exception::class)
    fun buildFactoryOnOtherPlayersPlanetThrowsException() {
        johnsHomePlanet!!.buildFactory(jack!!)
    }

    @Test
    @Throws(Exception::class)
    fun buildingFactoryReducesPlayerCredits() {
        doReturn(SUFFICIENT_CREDITS).`when`(john).credits
        doReturn(true).`when`(johnsFactorySite).hasAvailableSlots()

        johnsHomePlanet!!.buildFactory(john!!)
        verify(john).reduceCredits(Mockito.eq(SUFFICIENT_CREDITS))
    }

    @Test
    @Throws(Exception::class)
    fun noCreditsRemovedWhenNoFactorySlotsAvailable() {
        doReturn(false).`when`(johnsFactorySite).hasAvailableSlots()
        doThrow(IllegalActionException::class.java).`when`(johnsFactorySite).buildFactory()

        try {
            johnsHomePlanet!!.buildFactory(john!!)
            fail("Expected exception")
        } catch (e: IllegalActionException) {
            // expected behavior
        }

        verify(john, never()).reduceCredits(Mockito.anyInt())
    }

    @Test
    @Throws(Exception::class)
    fun cannotBuildFactoryWithInsufficientCredits_() {
        doReturn(true).`when`(johnsFactorySite).hasAvailableSlots()
        doThrow(InsufficientCreditsException::class.java).`when`(john).reduceCredits(Mockito.anyInt())

        try {
            johnsHomePlanet!!.buildFactory(john!!)
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
        doReturn(true).`when`(johnsFactorySite).hasAvailableSlots()

        assertThat(johnsHomePlanet!!.canBuildFactory(john!!), `is`(false))
        verify(john).credits
    }

    @Test
    @Throws(Exception::class)
    fun cannotBuildFactoryWithoutAvailableSlots() {
        doReturn(SUFFICIENT_CREDITS).`when`(john).credits
        doReturn(false).`when`(johnsFactorySite).hasAvailableSlots()

        assertThat(johnsHomePlanet!!.canBuildFactory(john!!), `is`(false))
        verify(johnsFactorySite).hasAvailableSlots()
    }

    @Test
    @Throws(Exception::class)
    fun cannotBuildFactoryOnOtherPlayersPlanet() {
        doReturn(SUFFICIENT_CREDITS).`when`(john).credits
        doReturn(true).`when`(johnsFactorySite).hasAvailableSlots()
        assertThat(johnsHomePlanet!!.canBuildFactory(jack!!), `is`(false))
    }

    @Test
    @Throws(Exception::class)
    fun cannotBuildFactoryOnUninhabitedPlanet() {
        doReturn(SUFFICIENT_CREDITS).`when`(john).credits
        doReturn(true).`when`(johnsFactorySite).hasAvailableSlots()
        assertThat(uninhabitedPlanet!!.canBuildFactory(john!!), `is`(false))
    }

    @Test
    @Throws(Exception::class)
    fun canBuildFactory() {
        doReturn(SUFFICIENT_CREDITS).`when`(john).credits
        doReturn(true).`when`(johnsFactorySite).hasAvailableSlots()
        assertThat(johnsHomePlanet!!.canBuildFactory(john!!), `is`(true))
    }

    @Test
    @Throws(Exception::class)
    fun canSetProductionFocusIfInhabitant() {
        johnsHomePlanet!!.setProductionFocus(1, john!!)
        verify(johnsFactorySite).shipProductionFocus = 1
    }

    @Test(expected = IllegalActionException::class)
    @Throws(Exception::class)
    fun cannotSetProductionFocusIfNotInhabitant() {
        johnsHomePlanet!!.setProductionFocus(1, jack!!)
    }

    companion object {

        private val SUFFICIENT_CREDITS = FactorySite.FACTORY_COST
        private val INSUFFICIENT_CREDITS = SUFFICIENT_CREDITS - 1
    }
}
