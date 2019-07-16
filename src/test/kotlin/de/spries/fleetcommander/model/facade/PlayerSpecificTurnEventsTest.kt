package de.spries.fleetcommander.model.facade

import com.nhaarman.mockitokotlin2.mock
import de.spries.fleetcommander.model.core.Player
import de.spries.fleetcommander.model.core.TurnEvents
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.verify

class PlayerSpecificTurnEventsTest {

    private lateinit var originalEvents: TurnEvents
    private lateinit var self: Player
    private lateinit var ownEvents: PlayerSpecificTurnEvents

    @Before
    fun setUp() {
        originalEvents = mock()
        self = mock()
        ownEvents = PlayerSpecificTurnEvents(originalEvents, self)
    }

    @Test
    fun forwardsCallToConqueredEnemyPlanets() {
        ownEvents.getConqueredEnemyPlanets()
        verify(originalEvents).getConqueredEnemyPlanets(self)
    }

    @Test
    fun forwardsCallToConqueredUninhabitedPlanets() {
        ownEvents.getConqueredUninhabitedPlanets()
        verify(originalEvents).getConqueredUninhabitedPlanets(self)
    }

    @Test
    fun forwardsCallToLostShipFormations() {
        ownEvents.getLostShipFormations()
        verify(originalEvents).getLostShipFormations(self)
    }

    @Test
    fun forwardsCallToDefendedPlanets() {
        ownEvents.getDefendedPlanets()
        verify(originalEvents).getDefendedPlanets(self)
    }

    @Test
    fun forwardsCallToLostPlanets() {
        ownEvents.getLostPlanets()
        verify(originalEvents).getLostPlanets(self)
    }

    @Test
    @Throws(Exception::class)
    fun forwardsCallToHasEvents() {
        ownEvents.hasEvents()
        verify(originalEvents).hasEvents(self)
    }

}
