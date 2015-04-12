package de.spries.fleetcommander.model.universe;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

public class UniverseTest {

	private Planet johnsHomePlanet;
	private Planet jacksHomePlanet;
	private Universe universe;

	@Before
	public void setUp() {
		johnsHomePlanet = mock(Planet.class);
		jacksHomePlanet = mock(Planet.class);
		doReturn(true).when(johnsHomePlanet).isHomePlanet();
		doReturn(true).when(jacksHomePlanet).isHomePlanet();
		universe = new Universe(Arrays.asList(johnsHomePlanet, jacksHomePlanet));
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void requiresPlanetList() {
		new Universe(null);
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void requiresNonEmptyPlanetList() {
		new Universe(Collections.emptyList());
	}

	@Test
	public void newUniverseHasPlanets() {
		assertThat(universe.getPlanets(), is(not(empty())));
	}

	@Test
	public void universeHasHomePlanets() throws Exception {
		Collection<Planet> homePlanets = universe.getHomePlanets();
		assertThat(homePlanets, hasItem(johnsHomePlanet));
		assertThat(homePlanets, hasItem(jacksHomePlanet));
		assertThat(homePlanets, hasSize(2));
	}

	@Test
	public void universeHasNoTravellingShips() throws Exception {
		assertThat(universe.getTravellingShipFormations(), hasSize(0));
	}
}
