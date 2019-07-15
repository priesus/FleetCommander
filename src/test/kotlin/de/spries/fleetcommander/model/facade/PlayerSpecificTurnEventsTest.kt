package de.spries.fleetcommander.model.facade

import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

import org.junit.Before
import org.junit.Test

import de.spries.fleetcommander.model.core.Player
import de.spries.fleetcommander.model.core.TurnEvents

class PlayerSpecificTurnEventsTest {

    private lateinit var originalEvents: TurnEvents
    private lateinit var self: Player
    private lateinit var ownEvents: PlayerSpecificTurnEvents

    @Before
    fun setUp() {
        originalEvents = mock(TurnEvents::class.java)
        self = mock(Player::class.java)
        ownEvents = PlayerSpecificTurnEvents(originalEvents!!, self!!)
    }

    @Test
    fun forwardsCallToConqueredEnemyPlanets() {
        ownEvents!!.conqueredEnemyPlanets
        verify(originalEvents).getConqueredEnemyPlanets(self!!)
    }

    @Test
    fun forwardsCallToConqueredUninhabitedPlanets() {
        ownEvents!!.conqueredUninhabitedPlanets
        verify(originalEvents).getConqueredUninhabitedPlanets(self!!)
    }

    @Test
    fun forwardsCallToLostShipFormations() {
        ownEvents!!.lostShipFormations
        verify(originalEvents).getLostShipFormations(self!!)
    }

    @Test
    fun forwardsCallToDefendedPlanets() {
        ownEvents!!.defendedPlanets
        verify(originalEvents).getDefendedPlanets(self!!)
    }

    @Test
    fun forwardsCallToLostPlanets() {
        ownEvents!!.lostPlanets
        verify(originalEvents).getLostPlanets(self!!)
    }

    @Test
    @Throws(Exception::class)
    fun forwardsCallToHasEvents() {
        ownEvents!!.hasEvents
        verify(originalEvents).hasEvents(self!!)
    }

}
