package de.spries.fleetcommander.model.core.universe

import de.spries.fleetcommander.model.core.Player
import de.spries.fleetcommander.model.core.common.IllegalActionException

class Planet @JvmOverloads constructor(x: Int = 0, y: Int = 0, private val planetClass: PlanetClass = PlanetClass.B, inhabitant: Player? = null, factorySite: FactorySite = FactorySite(planetClass))
    : HasCoordinates(x, y) {

    var id: Int = 0
    private var isHomePlanet = false
    private var inhabitant: Player? = null
    private var shipCount = 0f
    private val incomingShipsPerPlayer = mutableMapOf<Player, Int>()
    private val knownAsEnemyPlanetBy = mutableSetOf<Player>()
    private var isUnderAttack = false
    private var isJustInhabited = false
    private val factorySite: FactorySite

    private var turnEventBus: TurnEventBus? = null

    constructor(x: Int = 0, y: Int = 0, inhabitant: Player) : this(x, y, PlanetClass.B, inhabitant)
    constructor(x: Int = 0, y: Int = 0, inhabitant: Player, factorySite: FactorySite) : this(x, y, PlanetClass.B, inhabitant, factorySite)

    init {
        if (inhabitant != null) {
            shipCount = HOME_PLANET_STARTING_SHIPS
            this.inhabitant = inhabitant
            isHomePlanet = true
        }
        this.factorySite = factorySite
    }

    fun getPlanetClass() = planetClass
    fun isHomePlanet() = isHomePlanet
    fun inhabitant() = inhabitant
    fun isUnderAttack() = isUnderAttack
    fun isJustInhabited() = isJustInhabited
    fun getFactorySite() = factorySite

    fun isHomePlanetOf(player: Player): Boolean {
        return isHomePlanet && player == inhabitant
    }

    fun isInhabited() = inhabitant != null

    fun isInhabitedBy(player: Player): Boolean {
        return player == inhabitant
    }

    fun getShipCount(): Int {
        return shipCount.toInt()
    }

    fun runProductionCycle() {
        if (inhabitant != null) {
            shipCount += factorySite.getProducedShipsPerTurn()
            inhabitant!!.addCredits(factorySite.getProducedCreditsPerTurn())
        }
    }

    fun buildFactory(player: Player) {
        if (!canBuildFactory(player)) {
            throw IllegalActionException("You cannot build a factory right now")
        }
        player.reduceCredits(FactorySite.FACTORY_COST)
        factorySite.buildFactory()
    }

    fun canBuildFactory(player: Player): Boolean {
        if (player != inhabitant) {
            return false
        }
        if (!factorySite.hasAvailableSlots()) {
            return false
        }
        return player.getCredits() >= FactorySite.FACTORY_COST
    }

    fun setProductionFocus(focus: Int, player: Player) {
        if (player != inhabitant) {
            throw IllegalActionException("You can only change your own planets' production focus")
        }
        factorySite.updateShipProductionFocus(focus)
    }

    fun sendShipsAway(shipsToSend: Int, player: Player) {
        if (player != inhabitant) {
            throw IllegalActionException("You can only send ships from your own planets!")
        }
        if (shipsToSend > shipCount) {
            throw IllegalActionException("You don't have that many ships to send!")
        }

        shipCount -= shipsToSend.toFloat()
    }

    fun addIncomingShips(ships: Int, player: Player) {
        incomingShipsPerPlayer.putIfAbsent(player, 0)
        incomingShipsPerPlayer[player] = incomingShipsPerPlayer[player]!!.plus(ships)

    }

    fun getIncomingShipCount(player: Player): Int {
        val incShips = incomingShipsPerPlayer[player]
        return incShips ?: 0
    }

    fun landShips(shipsToLand: Int, invader: Player) {
        if (shipsToLand <= 0) {
            throw IllegalActionException("Cannot land $shipsToLand ships")
        }
        if (!isInhabited()) {
            turnEventBus!!.fireConqueredUninhabitedPlanet(invader)
            inhabitant = invader
            shipCount += shipsToLand.toFloat()
            knownAsEnemyPlanetBy.remove(invader)
            isJustInhabited = true
        } else if (isInhabitedBy(invader)) {
            shipCount += shipsToLand.toFloat()
        } else {
            // invasion
            shipCount -= shipsToLand.toFloat()
            if (shipCount < 0) {
                // Successful invasion
                turnEventBus!!.fireLostPlanet(inhabitant!!)
                turnEventBus!!.fireConqueredEnemyPlanet(invader)
                knownAsEnemyPlanetBy.add(inhabitant!!)
                inhabitant = invader
                shipCount *= -1f
                isHomePlanet = false
                knownAsEnemyPlanetBy.remove(invader)
                isJustInhabited = true
            } else {
                // Invaders defeated
                turnEventBus!!.fireDefendedPlanet(inhabitant!!)
                turnEventBus!!.fireLostShipFormation(invader)
                knownAsEnemyPlanetBy.add(invader)
                isUnderAttack = true
            }
        }
        addIncomingShips(shipsToLand * -1, invader)
    }

    fun resetMarkers() {
        isUnderAttack = false
        isJustInhabited = false
    }

    fun isKnownAsEnemyPlanet(viewingPlayer: Player): Boolean {
        return knownAsEnemyPlanetBy.contains(viewingPlayer)
    }

    fun setEventBus(turnEventBus: TurnEventBus) {
        this.turnEventBus = turnEventBus
    }

    fun handleDefeatedPlayer(defeatedPlayer: Player) {
        knownAsEnemyPlanetBy.remove(defeatedPlayer)
        incomingShipsPerPlayer.remove(defeatedPlayer)
        if (defeatedPlayer == inhabitant) {
            shipCount = 0f
            inhabitant = null
            isHomePlanet = false
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Planet

        if (planetClass != other.planetClass) return false
        if (id != other.id) return false
        if (isHomePlanet != other.isHomePlanet) return false
        if (inhabitant != other.inhabitant) return false
        if (shipCount != other.shipCount) return false
        if (incomingShipsPerPlayer != other.incomingShipsPerPlayer) return false
        if (knownAsEnemyPlanetBy != other.knownAsEnemyPlanetBy) return false
        if (isUnderAttack != other.isUnderAttack) return false
        if (isJustInhabited != other.isJustInhabited) return false
        if (factorySite != other.factorySite) return false

        return true
    }

    override fun hashCode(): Int {
        var result = planetClass.hashCode()
        result = 31 * result + id
        result = 31 * result + isHomePlanet.hashCode()
        result = 31 * result + (inhabitant?.hashCode() ?: 0)
        result = 31 * result + shipCount.hashCode()
        result = 31 * result + incomingShipsPerPlayer.hashCode()
        result = 31 * result + knownAsEnemyPlanetBy.hashCode()
        result = 31 * result + isUnderAttack.hashCode()
        result = 31 * result + isJustInhabited.hashCode()
        result = 31 * result + factorySite.hashCode()
        return result
    }

    override fun toString(): String {
        return "Planet(planetClass=$planetClass, id=$id, isHomePlanet=$isHomePlanet, inhabitant=$inhabitant)"
    }

    companion object {
        private const val HOME_PLANET_STARTING_SHIPS = 6f
    }
}