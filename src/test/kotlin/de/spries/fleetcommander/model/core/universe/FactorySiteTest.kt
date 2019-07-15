package de.spries.fleetcommander.model.core.universe

import org.hamcrest.Matchers.greaterThan
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertThat

import org.junit.Before
import org.junit.Test

import de.spries.fleetcommander.model.core.common.IllegalActionException

class FactorySiteTest {
    private lateinit var factorySite: FactorySite
    private lateinit var maxedOutFactorySite: FactorySite

    @Before
    @Throws(Exception::class)
    fun setUp() {
        factorySite = FactorySite(PlanetClass.B)
        maxedOutFactorySite = FactorySite(PlanetClass.B)
        for (i in 0 until factorySite!!.factorySlotCount) {
            maxedOutFactorySite!!.buildFactory()
        }
    }

    @Test(expected = IllegalActionException::class)
    @Throws(Exception::class)
    fun cannotBuildMoreFactoriesThanSlotsAvailable() {
        maxedOutFactorySite!!.buildFactory()
    }

    @Test
    @Throws(Exception::class)
    fun maxedOutFactorySiteHasNoMoreSlotsAvailable() {
        for (i in 0..5) {
            assertThat(factorySite!!.hasAvailableSlots(), `is`(true))
            factorySite!!.buildFactory()
        }

        assertThat(factorySite!!.hasAvailableSlots(), `is`(false))
    }

    @Test
    @Throws(Exception::class)
    fun buildingFactoriesDecreasesAvailableSlots() {
        for (i in 6 downTo 1) {
            assertThat(factorySite!!.availableSlots, `is`(i))
            factorySite!!.buildFactory()
        }

        assertThat(factorySite!!.availableSlots, `is`(0))
    }

    @Test
    @Throws(Exception::class)
    fun emptyFactorySiteHasNoFactories() {
        assertThat(factorySite!!.factoryCount, `is`(0))
    }

    @Test
    @Throws(Exception::class)
    fun factoryCountIncreasesWithEachBuiltFactory() {
        for (i in 0..5) {
            factorySite!!.buildFactory()
            assertThat(factorySite!!.factoryCount, `is`(i + 1))
        }
    }

    @Test
    @Throws(Exception::class)
    fun emptyFactorySiteProducesNoCredits() {
        assertThat(factorySite!!.producedCreditsPerTurn, `is`(0))
    }

    @Test
    @Throws(Exception::class)
    fun emptyFactorySiteProducesNoShips() {
        assertThat(factorySite!!.producedShipsPerTurn, `is`(0f))
    }

    @Test
    @Throws(Exception::class)
    fun factoryIncreasesCreditsProduction() {
        factorySite!!.buildFactory()
        assertThat(factorySite!!.producedCreditsPerTurn, `is`(greaterThan(0)))
    }

    @Test
    @Throws(Exception::class)
    fun factoryIncreasesShipProduction() {
        factorySite!!.buildFactory()
        assertThat(factorySite!!.producedShipsPerTurn, `is`(greaterThan(0f)))
    }

    @Test
    @Throws(Exception::class)
    fun initialProductionFocusIsBalanced50Percent() {
        assertThat(factorySite!!.shipProductionFocus, `is`(10))
    }

    @Test
    @Throws(Exception::class)
    fun fullProductionFocusOnShipsProducesShipsOnly() {
        factorySite!!.buildFactory()
        factorySite!!.shipProductionFocus = 20
        assertThat(factorySite!!.producedShipsPerTurn, `is`(SHIPS_PER_FACTORY_PER_TURN))
        assertThat(factorySite!!.producedCreditsPerTurn, `is`(0))
    }

    @Test
    @Throws(Exception::class)
    fun fullProductionFocusOnCreditsProducesCreditsOnly() {
        factorySite!!.buildFactory()
        factorySite!!.shipProductionFocus = 0
        assertThat(factorySite!!.producedCreditsPerTurn, `is`(CREDITS_PER_FACTORY_PER_TURN))
        assertThat(factorySite!!.producedShipsPerTurn, `is`(0f))
    }

    @Test
    @Throws(Exception::class)
    fun balancedProductionFocusProducesBothShipsAndCredits() {
        factorySite!!.buildFactory()
        factorySite!!.shipProductionFocus = 10
        assertThat(factorySite!!.producedCreditsPerTurn, `is`(CREDITS_PER_FACTORY_PER_TURN / 2))
        assertThat(factorySite!!.producedShipsPerTurn, `is`(SHIPS_PER_FACTORY_PER_TURN / 2))
    }

    @Test
    @Throws(Exception::class)
    fun differentPlanetClassProducesDifferentResources() {
        val factorySite = FactorySite(PlanetClass.P)
        factorySite.buildFactory()
        factorySite.shipProductionFocus = 10
        assertThat(factorySite.producedCreditsPerTurn, `is`(PlanetClass.P.creditsPerFactoryPerTurn / 2))
        assertThat(factorySite.producedShipsPerTurn, `is`(PlanetClass.P.shipsPerFactoryPerTurn / 2))
    }

    @Test
    @Throws(Exception::class)
    fun shipProductionFocusProducesMoreShipsThanCredits() {
        factorySite!!.buildFactory()
        factorySite!!.shipProductionFocus = 15
        assertThat(factorySite!!.producedCreditsPerTurn, `is`(CREDITS_PER_FACTORY_PER_TURN / 4 * 1))
        assertThat(factorySite!!.producedShipsPerTurn, `is`(SHIPS_PER_FACTORY_PER_TURN / 4 * 3))
    }

    @Test
    @Throws(Exception::class)
    fun creditProductionFocusProducesMoreCreditsThanShips() {
        factorySite!!.buildFactory()
        factorySite!!.shipProductionFocus = 5
        assertThat(factorySite!!.producedCreditsPerTurn, `is`(CREDITS_PER_FACTORY_PER_TURN / 4 * 3))
        assertThat(factorySite!!.producedShipsPerTurn, `is`(SHIPS_PER_FACTORY_PER_TURN / 4 * 1))
    }

    @Test(expected = IllegalActionException::class)
    @Throws(Exception::class)
    fun cannotSetNegativeProductionFocus() {
        factorySite!!.shipProductionFocus = -1
    }

    @Test(expected = IllegalActionException::class)
    @Throws(Exception::class)
    fun cannotSetProductionFocusGreaterThan20() {
        factorySite!!.shipProductionFocus = 21
    }

    companion object {

        private val SHIPS_PER_FACTORY_PER_TURN = PlanetClass.B.shipsPerFactoryPerTurn
        private val CREDITS_PER_FACTORY_PER_TURN = PlanetClass.B.creditsPerFactoryPerTurn
    }
}
