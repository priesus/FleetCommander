package de.spries.fleetcommander.model.core.universe

import de.spries.fleetcommander.model.core.Game
import de.spries.fleetcommander.model.core.Player
import java.util.ArrayList
import java.util.Random

object UniverseFactory {
    private const val PLANET_COUNT = 100
    private const val MIN_PLANET_OFFSET = -2
    private const val MAX_PLANET_OFFSET = 2
    private val rand = Random()

    /**
     * Prototype implementation for testing purposes
     */
    fun generate(players: Collection<Player>): Universe {
        var planets: MutableList<Planet> = ArrayList(PLANET_COUNT)

        for (row in 0..9) {
            for (col in 0..9) {
                val offsetX = randomOffset()
                val offsetY = randomOffset()

                val planet = Planet(col * 10 + 5 + offsetX, row * 10 + 5 + offsetY)
                planets.add(planet)
            }
        }

        planets.shuffle()

        planets = planets.subList(0, planets.size / (1 + Game.MAX_PLAYERS - players.size))

        var planetId = 0
        for (planet in planets) {
            planet.id = planetId++
        }

        for ((i, player) in players.withIndex()) {
            val oldUninhabitedPlanet = planets[i]
            val newHomePlanet = Planet(oldUninhabitedPlanet.x, oldUninhabitedPlanet.y, player)
            newHomePlanet.id = oldUninhabitedPlanet.id
            planets[i] = newHomePlanet
        }

        return Universe(planets)
    }

    private fun randomOffset(): Int {
        return rand.nextInt(MAX_PLANET_OFFSET - MIN_PLANET_OFFSET + 1) + MIN_PLANET_OFFSET
    }

}