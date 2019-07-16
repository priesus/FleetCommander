package de.spries.fleetcommander.model.core.universe

import de.spries.fleetcommander.model.core.common.IllegalActionException
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.greaterThan
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test

class FactorySiteTest {
    private lateinit var factorySite: FactorySite
    private lateinit var maxedOutFactorySite: FactorySite

    @Before
    fun setUp() {
        factorySite = FactorySite(PlanetClass.B)
        maxedOutFactorySite = FactorySite(PlanetClass.B)
        for (i in 0 until factorySite.getFactorySlotCount()) {
            maxedOutFactorySite.buildFactory()
        }
    }

    @Test(expected = IllegalActionException::class)
    fun cannotBuildMoreFactoriesThanSlotsAvailable() {
        maxedOutFactorySite.buildFactory()
    }

    @Test
    fun maxedOutFactorySiteHasNoMoreSlotsAvailable() {
        for (i in 0..5) {
            assertThat(factorySite.hasAvailableSlots(), `is`(true))
            factorySite.buildFactory()
        }

        assertThat(factorySite.hasAvailableSlots(), `is`(false))
    }

    @Test
    fun buildingFactoriesDecreasesAvailableSlots() {
        for (i in 6 downTo 1) {
            assertThat(factorySite.getAvailableSlots(), `is`(i))
            factorySite.buildFactory()
        }

        assertThat(factorySite.getAvailableSlots(), `is`(0))
    }

    @Test
    fun emptyFactorySiteHasNoFactories() {
        assertThat(factorySite.getFactoryCount(), `is`(0))
    }

    @Test
    fun factoryCountIncreasesWithEachBuiltFactory() {
        for (i in 0..5) {
            factorySite.buildFactory()
            assertThat(factorySite.getFactoryCount(), `is`(i + 1))
        }
    }

    @Test
    fun emptyFactorySiteProducesNoCredits() {
        assertThat(factorySite.getProducedCreditsPerTurn(), `is`(0))
    }

    @Test
    fun emptyFactorySiteProducesNoShips() {
        assertThat(factorySite.getProducedShipsPerTurn(), `is`(0f))
    }

    @Test
    fun factoryIncreasesCreditsProduction() {
        factorySite.buildFactory()
        assertThat(factorySite.getProducedCreditsPerTurn(), `is`(greaterThan(0)))
    }

    @Test
    fun factoryIncreasesShipProduction() {
        factorySite.buildFactory()
        assertThat(factorySite.getProducedShipsPerTurn(), `is`(greaterThan(0f)))
    }

    @Test
    fun initialProductionFocusIsBalanced50Percent() {
        assertThat(factorySite.getShipProductionFocus(), `is`(10))
    }

    @Test
    fun fullProductionFocusOnShipsProducesShipsOnly() {
        factorySite.buildFactory()
        factorySite.updateShipProductionFocus(20)
        assertThat(factorySite.getProducedShipsPerTurn(), `is`(SHIPS_PER_FACTORY_PER_TURN))
        assertThat(factorySite.getProducedCreditsPerTurn(), `is`(0))
    }

    @Test
    fun fullProductionFocusOnCreditsProducesCreditsOnly() {
        factorySite.buildFactory()
        factorySite.updateShipProductionFocus(0)
        assertThat(factorySite.getProducedCreditsPerTurn(), `is`(CREDITS_PER_FACTORY_PER_TURN))
        assertThat(factorySite.getProducedShipsPerTurn(), `is`(0f))
    }

    @Test
    fun balancedProductionFocusProducesBothShipsAndCredits() {
        factorySite.buildFactory()
        factorySite.updateShipProductionFocus(10)
        assertThat(factorySite.getProducedCreditsPerTurn(), `is`(CREDITS_PER_FACTORY_PER_TURN / 2))
        assertThat(factorySite.getProducedShipsPerTurn(), `is`(SHIPS_PER_FACTORY_PER_TURN / 2))
    }

    @Test
    fun differentPlanetClassProducesDifferentResources() {
        val factorySite = FactorySite(PlanetClass.P)
        factorySite.buildFactory()
        factorySite.updateShipProductionFocus(10)
        assertThat(factorySite.getProducedCreditsPerTurn(), `is`(PlanetClass.P.getCreditsPerFactoryPerTurn() / 2))
        assertThat(factorySite.getProducedShipsPerTurn(), `is`(PlanetClass.P.getShipsPerFactoryPerTurn() / 2))
    }

    @Test
    fun shipProductionFocusProducesMoreShipsThanCredits() {
        factorySite.buildFactory()
        factorySite.updateShipProductionFocus(15)
        assertThat(factorySite.getProducedCreditsPerTurn(), `is`(CREDITS_PER_FACTORY_PER_TURN / 4 * 1))
        assertThat(factorySite.getProducedShipsPerTurn(), `is`(SHIPS_PER_FACTORY_PER_TURN / 4 * 3))
    }

    @Test
    fun creditProductionFocusProducesMoreCreditsThanShips() {
        factorySite.buildFactory()
        factorySite.updateShipProductionFocus(5)
        assertThat(factorySite.getProducedCreditsPerTurn(), `is`(CREDITS_PER_FACTORY_PER_TURN / 4 * 3))
        assertThat(factorySite.getProducedShipsPerTurn(), `is`(SHIPS_PER_FACTORY_PER_TURN / 4 * 1))
    }

    @Test(expected = IllegalActionException::class)
    fun cannotSetNegativeProductionFocus() {
        factorySite.updateShipProductionFocus(-1)
    }

    @Test(expected = IllegalActionException::class)
    fun cannotSetProductionFocusGreaterThan20() {
        factorySite.updateShipProductionFocus(21)
    }

    companion object {

        private val SHIPS_PER_FACTORY_PER_TURN = PlanetClass.B.getShipsPerFactoryPerTurn()
        private val CREDITS_PER_FACTORY_PER_TURN = PlanetClass.B.getCreditsPerFactoryPerTurn()
    }
}
