package de.spries.fleetcommander.model.core.universe

import com.nhaarman.mockitokotlin2.mock
import de.spries.fleetcommander.model.core.Player
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.greaterThan
import org.hamcrest.Matchers.notNullValue
import org.hamcrest.Matchers.nullValue
import org.junit.Assert.assertThat
import org.junit.Test

class UniverseFactoryTest {

    @Test
    fun generatedUniverseHasMorePlanetsThanPlayers() {
        val universe = UniverseFactory.generate(JOHN_ONLY)
        assertThat(universe.getPlanets().size, greaterThan(JOHN_ONLY.size))
    }

    @Test
    fun playerHasHomePlanet() {
        val universe = UniverseFactory.generate(JOHN_ONLY)
        assertThat(universe.getHomePlanetOf(JOHN), `is`(notNullValue()))
    }

    @Test
    fun playersNotParticipatingInGameDontHaveAHomePlanet() {
        val universe = UniverseFactory.generate(JOHN_ONLY)
        assertThat(universe.getHomePlanetOf(OTHER_PLAYER), `is`(nullValue()))
    }

    @Test
    fun everyPlanetHasAUniqueId() {
        val universe = UniverseFactory.generate(JOHN_ONLY)
        val planetIds = universe.getPlanets().map { p -> p.id }

        assertThat(planetIds.size, `is`(universe.getPlanets().size))
    }

    companion object {

        private val JOHN = mock<Player>()
        private val OTHER_PLAYER = mock<Player>()
        private val JOHN_ONLY = listOf(JOHN)
    }
}
