package de.spries.fleetcommander.model.core.universe

import com.nhaarman.mockitokotlin2.mock
import de.spries.fleetcommander.model.core.Player
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.closeTo
import org.hamcrest.Matchers.nullValue
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test

class PlanetTest {

    private lateinit var jack: Player
    private lateinit var john: Player
    private lateinit var jim: Player
    private lateinit var johnsHomePlanet: Planet
    private lateinit var uninhabitedPlanet: Planet

    @Before
    fun setUp() {
        john = mock()
        jack = mock()
        jim = mock()

        johnsHomePlanet = Planet(0, 0, john)
        uninhabitedPlanet = Planet(0, 0)

        johnsHomePlanet.setEventBus(mock())
    }

    @Test
    fun newPlanetHasCoordinates() {
        val planet = Planet(10, 20)
        assertThat(planet.x, `is`<Number>(10))
        assertThat(planet.y, `is`<Number>(20))
    }

    @Test
    @Throws(Exception::class)
    fun planetsWithInhabitantAreInhabited() {
        assertThat(uninhabitedPlanet.isInhabited(), `is`(false))
        assertThat(johnsHomePlanet.isInhabited(), `is`(true))
    }

    @Test
    @Throws(Exception::class)
    fun distanceIsCalculatedThroughPythagoras() {
        val p1 = Planet(0, 0)
        val p2 = Planet(10, 10)
        val p3 = Planet(10, 0)
        val p4 = Planet(0, 0)

        assertThat(p1.distanceTo(p2), `is`(closeTo(14.14, 0.01)))
        assertThat(p1.distanceTo(p3), `is`(10.0))
        assertThat(p1.distanceTo(p4), `is`(0.0))
    }

    @Test
    @Throws(Exception::class)
    fun homePlanetIsIdentifiable() {
        assertThat(johnsHomePlanet.isHomePlanetOf(john), `is`(true))
        assertThat(johnsHomePlanet.isHomePlanetOf(jack), `is`(false))

        assertThat(uninhabitedPlanet.isHomePlanetOf(john), `is`(false))
        assertThat(uninhabitedPlanet.isHomePlanetOf(jack), `is`(false))
    }

    @Test
    @Throws(Exception::class)
    fun homePlanetIsOwnedByInhabitingPlayerOnly() {
        assertThat(johnsHomePlanet.isInhabitedBy(john), `is`(true))
        assertThat(johnsHomePlanet.isInhabitedBy(jack), `is`(false))
    }

    @Test
    @Throws(Exception::class)
    fun inhabitantIsRemovedWhenPlayerIsDefeated() {
        johnsHomePlanet.handleDefeatedPlayer(john)
        assertThat(johnsHomePlanet.inhabitant(), `is`(nullValue()))
    }

    @Test
    @Throws(Exception::class)
    fun defeatedPlayerHasNoEffectOnOtherPlayersPlanetInhabitant() {
        johnsHomePlanet.handleDefeatedPlayer(jack)
        assertThat(johnsHomePlanet.inhabitant(), `is`(john))
    }

    @Test
    @Throws(Exception::class)
    fun shipsAreRemovedForDefeatedPlayer() {
        johnsHomePlanet.handleDefeatedPlayer(john)
        assertThat(johnsHomePlanet.getShipCount(), `is`(0))
    }

    @Test
    @Throws(Exception::class)
    fun shipsAreNotAffectedForOtherDefeatedPlayers() {
        johnsHomePlanet.handleDefeatedPlayer(jack)
        assertThat(johnsHomePlanet.getShipCount(), `is`(6))
    }

    @Test
    @Throws(Exception::class)
    fun enemyMarkerIsRemovedForDefeatedPlayers() {
        johnsHomePlanet.landShips(1, jack)
        johnsHomePlanet.handleDefeatedPlayer(jack)
        assertThat(johnsHomePlanet.isKnownAsEnemyPlanet(jack), `is`(false))
    }

    @Test
    @Throws(Exception::class)
    fun enemyMarkerIsNotAffectedForOtherDefeatedPlayers() {
        johnsHomePlanet.landShips(1, jim)
        johnsHomePlanet.handleDefeatedPlayer(jack)
        assertThat(johnsHomePlanet.isKnownAsEnemyPlanet(jim), `is`(true))
    }

    @Test
    @Throws(Exception::class)
    fun incomingShipsAreRemovedForDefeatedPlayers() {
        johnsHomePlanet.addIncomingShips(1, jack)
        johnsHomePlanet.handleDefeatedPlayer(jack)
        assertThat(johnsHomePlanet.getIncomingShipCount(jack), `is`(0))
    }

    @Test
    @Throws(Exception::class)
    fun incomingShipsAreNotAffectedForOtherDefeatedPlayers() {
        johnsHomePlanet.addIncomingShips(1, jim)
        johnsHomePlanet.handleDefeatedPlayer(jack)
        assertThat(johnsHomePlanet.getIncomingShipCount(jim), `is`(1))
    }

    @Test
    @Throws(Exception::class)
    fun homePlanetIsNeutralizedForDefeatedPlayers() {
        johnsHomePlanet.handleDefeatedPlayer(john)
        assertThat(johnsHomePlanet.isHomePlanet(), `is`(false))
    }

}
