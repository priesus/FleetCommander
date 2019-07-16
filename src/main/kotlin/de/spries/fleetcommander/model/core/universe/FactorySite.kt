package de.spries.fleetcommander.model.core.universe

import de.spries.fleetcommander.model.core.common.IllegalActionException

class FactorySite(private val planetClass: PlanetClass) {
    private var factoryCount = 0
    private var shipProductionFocus = MAX_PRODUCTION_FOCUS / 2
        set(prodFocus) {
            if (prodFocus < 0 || prodFocus > MAX_PRODUCTION_FOCUS) {
                throw IllegalActionException("Production focus of factory sites must be between 0 and $MAX_PRODUCTION_FOCUS")
            }
            field = prodFocus
        }

    fun getFactoryCount() = factoryCount
    fun getShipProductionFocus() = shipProductionFocus
    fun updateShipProductionFocus(shipProductionFocus: Int) {
        this.shipProductionFocus = shipProductionFocus
    }

    fun factorySlotCount() = FACTORY_SLOTS

    fun getProducedCreditsPerTurn(): Int {
        val creditsProductionFocus = MAX_PRODUCTION_FOCUS - this.shipProductionFocus
        return factoryCount * planetClass.getCreditsPerFactoryPerTurn() * creditsProductionFocus / MAX_PRODUCTION_FOCUS
    }

    fun getProducedShipsPerTurn() = factoryCount.toFloat() * planetClass.getShipsPerFactoryPerTurn() * this.shipProductionFocus.toFloat() / MAX_PRODUCTION_FOCUS

    fun getAvailableSlots() = FACTORY_SLOTS - factoryCount

    fun buildFactory() {
        if (FACTORY_SLOTS == factoryCount) {
            throw IllegalActionException("No more space for factories!")
        }
        factoryCount++
    }

    fun hasAvailableSlots(): Boolean {
        return factoryCount < FACTORY_SLOTS
    }

    companion object {

        const val FACTORY_COST = 100
        const val MAX_PRODUCTION_FOCUS = 20
        const val FACTORY_SLOTS = 6
    }
}
