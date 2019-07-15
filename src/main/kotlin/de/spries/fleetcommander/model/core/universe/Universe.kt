package de.spries.fleetcommander.model.core.universe

import de.spries.fleetcommander.model.core.Player
import de.spries.fleetcommander.model.core.common.IllegalActionException
import org.apache.commons.collections4.CollectionUtils
import java.util.Collections

class Universe(planets: List<Planet>) {

    val planets: List<Planet>
    val homePlanets: Collection<Planet>
        get() = Planet.filterHomePlanets(planets)
    val travellingShipFormations: MutableCollection<ShipFormation> = mutableSetOf()

    init {
        if (CollectionUtils.isEmpty(planets)) {
            throw IllegalArgumentException("List of planets required")
        }
        this.planets = Collections.unmodifiableList(planets)
    }

    fun getHomePlanetOf(player: Player): Planet? {
        return planets.firstOrNull { it.isHomePlanetOf(player) }
    }

    fun runFactoryProductionCycle() {
        planets.forEach { p -> p.runProductionCycle() }
    }

    fun runShipTravellingCycle() {
        travellingShipFormations
                .sortedBy { it.distanceRemaining }
                .forEach { it.travel() }
        travellingShipFormations.removeIf { it.hasArrived() }
    }

    fun resetPreviousTurnMarkers() {
        planets.forEach { p -> p.resetMarkers() }
    }

    fun sendShips(shipCount: Int, originPlanetId: Int, destinationPlanetId: Int, player: Player) {
        val origin = getPlanetForId(originPlanetId)
        val destination = getPlanetForId(destinationPlanetId)
        sendShips(shipCount, origin, destination, player)
    }

    fun sendShips(shipCount: Int, origin: Planet, destination: Planet, player: Player) {
        if (!planets.contains(origin) || !planets.contains(destination)) {
            throw IllegalActionException("origin & destination planets must be contained in universe")
        }

        if (destination == origin) {
            return
        }

        origin.sendShipsAway(shipCount, player)
        destination.addIncomingShips(shipCount, player)

        val newShipFormation = ShipFormation(shipCount, origin, destination, player)
        val joinableFormation = getJoinableShipFormation(newShipFormation)

        if (joinableFormation == null) {
            travellingShipFormations.add(newShipFormation)
        } else {
            newShipFormation.join(joinableFormation)
        }
    }

    fun getPlanetForId(planetId: Int): Planet {
        return Planet.findById(planets, planetId)
    }

    private fun getJoinableShipFormation(newShipFormation: ShipFormation): ShipFormation? {
        for (formation in travellingShipFormations) {
            if (newShipFormation.canJoin(formation)) {
                return formation
            }
        }
        return null
    }

    fun setEventBus(turnEventBus: TurnEventBus) {
        planets.forEach { p -> p.setEventBus(turnEventBus) }
    }

    fun handleDefeatedPlayer(newDefeatedPlayer: Player) {
        planets.forEach { p -> p.handleDefeatedPlayer(newDefeatedPlayer) }
        travellingShipFormations.removeIf { it.commander == newDefeatedPlayer }
    }
}