package de.spries.fleetcommander.model.core;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class TurnEventsTest {

	private TurnEvents events;
	private Player player;
	private Player otherPlayer;

	@Before
	public void setUp() {
		player = mock(Player.class);
		otherPlayer = mock(Player.class);
		events = new TurnEvents(Arrays.asList(player, otherPlayer));
	}

	@Test
	public void hasNoEventsInitially() {
		assertThat(events.hasEvents(player), is(false));
	}

	@Test
	public void hasEventsWhenHasConqueredEnemyPlanets() throws Exception {
		events.fireConqueredEnemyPlanet(player);
		assertThat(events.getConqueredEnemyPlanets(player), is(1));
		assertThat(events.hasEvents(player), is(true));
	}

	@Test
	public void hasEventsWhenHasConqueredUninhabitedPlanets() throws Exception {
		events.fireConqueredUninhabitedPlanet(player);
		assertThat(events.getConqueredUninhabitedPlanets(player), is(1));
		assertThat(events.hasEvents(player), is(true));
	}

	@Test
	public void hasEventsWhenHasLostShipFormations() throws Exception {
		events.fireLostShipFormation(player);
		assertThat(events.getLostShipFormations(player), is(1));
		assertThat(events.hasEvents(player), is(true));
	}

	@Test
	public void hasEventsWhenHasDefendedPlanets() throws Exception {
		events.fireDefendedPlanet(player);
		assertThat(events.getDefendedPlanets(player), is(1));
		assertThat(events.hasEvents(player), is(true));
	}

	@Test
	public void hasEventsWhenHasLostPlanets() throws Exception {
		events.fireLostPlanet(player);
		assertThat(events.getLostPlanets(player), is(1));
		assertThat(events.hasEvents(player), is(true));
	}

	@Test
	public void addingEventsForPlayerDoesntChangeValuesForOtherPlayer() throws Exception {
		events.fireConqueredEnemyPlanet(otherPlayer);
		events.fireConqueredUninhabitedPlanet(otherPlayer);
		events.fireDefendedPlanet(otherPlayer);
		events.fireLostShipFormation(otherPlayer);
		events.fireLostPlanet(otherPlayer);

		assertThat(events.hasEvents(player), is(false));
	}

	@Test
	public void clearingEventsSetsEventsToZero() throws Exception {
		events.fireConqueredEnemyPlanet(player);
		events.fireConqueredUninhabitedPlanet(player);
		events.fireDefendedPlanet(player);
		events.fireLostShipFormation(player);
		events.fireLostPlanet(player);

		events.clear();
		assertThat(events.hasEvents(player), is(false));
	}

}
