package de.spries.fleetcommander.model.core

import com.nhaarman.mockitokotlin2.mock
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test

class TurnEventsTest {

    private lateinit var events: TurnEvents
    private lateinit var player: Player
    private lateinit var otherPlayer: Player

    @Before
    fun setUp() {
        player = mock()
        otherPlayer = mock()
        events = TurnEvents(listOf(player, otherPlayer))
    }

    @Test
    fun hasNoEventsInitially() {
        assertThat(events.hasEvents(player), `is`(false))
    }

    @Test
    fun hasEventsWhenHasConqueredEnemyPlanets() {
        events.fireConqueredEnemyPlanet(player)
        assertThat(events.getConqueredEnemyPlanets(player), `is`(1))
        assertThat(events.hasEvents(player), `is`(true))
    }

    @Test
    fun hasEventsWhenHasConqueredUninhabitedPlanets() {
        events.fireConqueredUninhabitedPlanet(player)
        assertThat(events.getConqueredUninhabitedPlanets(player), `is`(1))
        assertThat(events.hasEvents(player), `is`(true))
    }

    @Test
    fun hasEventsWhenHasLostShipFormations() {
        events.fireLostShipFormation(player)
        assertThat(events.getLostShipFormations(player), `is`(1))
        assertThat(events.hasEvents(player), `is`(true))
    }

    @Test
    fun hasEventsWhenHasDefendedPlanets() {
        events.fireDefendedPlanet(player)
        assertThat(events.getDefendedPlanets(player), `is`(1))
        assertThat(events.hasEvents(player), `is`(true))
    }

    @Test
    fun hasEventsWhenHasLostPlanets() {
        events.fireLostPlanet(player)
        assertThat(events.getLostPlanets(player), `is`(1))
        assertThat(events.hasEvents(player), `is`(true))
    }

    @Test
    fun addingEventsForPlayerDoesntChangeValuesForOtherPlayer() {
        events.fireConqueredEnemyPlanet(otherPlayer)
        events.fireConqueredUninhabitedPlanet(otherPlayer)
        events.fireDefendedPlanet(otherPlayer)
        events.fireLostShipFormation(otherPlayer)
        events.fireLostPlanet(otherPlayer)

        assertThat(events.hasEvents(player), `is`(false))
    }

    @Test
    fun clearingEventsSetsEventsToZero() {
        events.fireConqueredEnemyPlanet(player)
        events.fireConqueredUninhabitedPlanet(player)
        events.fireDefendedPlanet(player)
        events.fireLostShipFormation(player)
        events.fireLostPlanet(player)

        events.clear()
        assertThat(events.hasEvents(player), `is`(false))
    }

}
