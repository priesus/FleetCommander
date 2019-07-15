package de.spries.fleetcommander.model.core.universe

import org.hamcrest.Matchers.greaterThan
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.hamcrest.Matchers.nullValue
import org.junit.Assert.assertThat
import org.mockito.Mockito.mock

import java.util.Arrays
import java.util.stream.Collectors

import org.junit.Test

import de.spries.fleetcommander.model.core.Player

class UniverseFactoryTest {

    @Test
    @Throws(Exception::class)
    fun generatedUniverseHasMorePlanetsThanPlayers() {
        val universe = UniverseFactory.generate(JOHN_ONLY)
        assertThat(universe.planets.size, greaterThan(JOHN_ONLY.size))
    }

    @Test
    @Throws(Exception::class)
    fun playerHasHomePlanet() {
        val universe = UniverseFactory.generate(JOHN_ONLY)
        assertThat(universe.getHomePlanetOf(JOHN), `is`(notNullValue()))
    }

    @Test
    @Throws(Exception::class)
    fun playersNotParticipatingInGameDontHaveAHomePlanet() {
        val universe = UniverseFactory.generate(JOHN_ONLY)
        assertThat(universe.getHomePlanetOf(OTHER_PLAYER), `is`(nullValue()))
    }

    @Test
    @Throws(Exception::class)
    fun everyPlanetHasAUniqueId() {
        val universe = UniverseFactory.generate(JOHN_ONLY)
        val planetIds = universe.planets.map { p -> p.id }

        assertThat(planetIds.size, `is`(universe.planets.size))
    }

    companion object {

        private val JOHN = mock(Player::class.java)
        private val OTHER_PLAYER = mock(Player::class.java)
        private val JOHN_ONLY = Arrays.asList(JOHN)
    }
}