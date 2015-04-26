package de.spries.fleetcommander.model.facade;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import de.spries.fleetcommander.model.core.Player;
import de.spries.fleetcommander.model.core.TurnEvents;

public class PlayerSpecificTurnEventsTest {

	private TurnEvents originalEvents;
	private Player self;
	private PlayerSpecificTurnEvents ownEvents;

	@Before
	public void setUp() {
		originalEvents = mock(TurnEvents.class);
		self = mock(Player.class);
		ownEvents = new PlayerSpecificTurnEvents(originalEvents, self);
	}

	@Test
	public void forwardsCallToConqueredEnemyPlanets() {
		ownEvents.getConqueredEnemyPlanets();
		verify(originalEvents).getConqueredEnemyPlanets(self);
	}

	@Test
	public void forwardsCallToConqueredUninhabitedPlanets() {
		ownEvents.getConqueredUninhabitedPlanets();
		verify(originalEvents).getConqueredUninhabitedPlanets(self);
	}

	@Test
	public void forwardsCallToLostShipFormations() {
		ownEvents.getLostShipFormations();
		verify(originalEvents).getLostShipFormations(self);
	}

	@Test
	public void forwardsCallToDefendedPlanets() {
		ownEvents.getDefendedPlanets();
		verify(originalEvents).getDefendedPlanets(self);
	}

	@Test
	public void forwardsCallToLostPlanets() {
		ownEvents.getLostPlanets();
		verify(originalEvents).getLostPlanets(self);
	}

	@Test
	public void forwardsCallToHasEvents() throws Exception {
		ownEvents.getHasEvents();
		verify(originalEvents).hasEvents(self);
	}

}
