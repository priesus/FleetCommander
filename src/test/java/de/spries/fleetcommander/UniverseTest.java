package de.spries.fleetcommander;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collections;

import org.hamcrest.Matchers;
import org.junit.Test;

public class UniverseTest {

	private static final Planet PLANET1 = new Planet(0, 0);
	private static final Planet PLANET2 = new Planet(5, 5);

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
		Universe universe = new Universe(Arrays.asList(PLANET1, PLANET2));
		assertThat(universe.getPlanets(), hasSize(2));
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void planetsMustHaveDifferentPosition() {
		new Universe(Arrays.asList(new Planet(0, 0), new Planet(0, 0)));
	}

	@Test
	public void onlyPlanetsInhabitedByPlayerAreReturned() throws Exception {
		Player john = new Player("John");
		Player jack = new Player("Jack");

		Planet uninhabitedPlanet = new Planet(0, 0);
		Planet johnsPlanet = new Planet(1, 0, john);
		Planet JacksPlanet = new Planet(2, 0, jack);

		Universe universe = new Universe(Arrays.asList(uninhabitedPlanet, johnsPlanet, JacksPlanet));
		assertThat(universe.getPlanetsInhabitedBy(john), Matchers.contains(johnsPlanet));
	}

}
