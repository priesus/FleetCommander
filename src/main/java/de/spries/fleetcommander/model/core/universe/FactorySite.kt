package de.spries.fleetcommander.model.core.universe

import de.spries.fleetcommander.model.core.common.IllegalActionException

class FactorySite(private val planetClass: PlanetClass) {
    var factoryCount = 0
        private set
    var shipProductionFocus = MAX_PRODUCTION_FOCUS / 2
        set(prodFocus) {
            if (prodFocus < 0 || prodFocus > MAX_PRODUCTION_FOCUS) {
                throw IllegalActionException("Production focus of factory sites must be between 0 and $MAX_PRODUCTION_FOCUS")
            }
            field = prodFocus
        }

    val factorySlotCount: Int
        get() = FACTORY_SLOTS

    val producedCreditsPerTurn: Int
        get() {
            val creditsProductionFocus = MAX_PRODUCTION_FOCUS - this.shipProductionFocus
            return factoryCount * planetClass.creditsPerFactoryPerTurn * creditsProductionFocus / MAX_PRODUCTION_FOCUS
        }

    val producedShipsPerTurn: Float
        get() = factoryCount.toFloat() * planetClass.shipsPerFactoryPerTurn * this.shipProductionFocus.toFloat() / MAX_PRODUCTION_FOCUS

    val availableSlots: Int
        get() = FACTORY_SLOTS - factoryCount

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
