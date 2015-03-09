package de.spries.fleetcommander;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import de.spries.fleetcommander.Planet.NoFactorySlotsAvailableException;
import de.spries.fleetcommander.Planet.NotPlayersOwnPlanetException;

public class PlanetTest {

	private Player jack;
	private Player john;
	private Planet johnsHomePlanet;
	private Planet uninhabitedPlanet;

	@Before
	public void setUp() {
		john = new Player("John");
		jack = new Player("Jack");

		johnsHomePlanet = new Planet(0, 0, john);
		uninhabitedPlanet = new Planet(0, 0);
	}

	@Test
	public void newPlanetHasCoordinates() {
		Planet planet = new Planet(10, 20);
		assertThat(planet.getCoordinateX(), is(10));
		assertThat(planet.getCoordinateY(), is(20));
	}

	@Test
	public void distanceIsCalculatedThroughPythagoras() throws Exception {
		Planet p1 = new Planet(0, 0);
		Planet p2 = new Planet(10, 10);
		Planet p3 = new Planet(10, 0);
		Planet p4 = new Planet(0, 0);

		assertThat(p1.distanceTo(p2), is(closeTo(14.14, 0.01)));
		assertThat(p1.distanceTo(p3), is(10.));
		assertThat(p1.distanceTo(p4), is(0.));
	}

	@Test
	public void homePlanetIsIdentifiable() throws Exception {
		assertThat(johnsHomePlanet.isHomePlanetOf(john), is(true));
		assertThat(johnsHomePlanet.isHomePlanetOf(jack), is(false));

		assertThat(uninhabitedPlanet.isHomePlanetOf(john), is(false));
		assertThat(uninhabitedPlanet.isHomePlanetOf(jack), is(false));
	}

	@Test
	public void homePlanetIsOwnedByInhabitingPlayerOnly() throws Exception {
		assertThat(johnsHomePlanet.isInhabitedBy(john), is(true));
		assertThat(johnsHomePlanet.isInhabitedBy(jack), is(false));
	}

	@Test(expected = NotPlayersOwnPlanetException.class)
	public void cannotBuildFactoryOnUninhabitedPlanet() throws Exception {
		uninhabitedPlanet.buildFactory(john);
	}

	@Test(expected = NotPlayersOwnPlanetException.class)
	public void cannotBuildFactoryOnOtherPlayersPlanet() throws Exception {
		johnsHomePlanet.buildFactory(jack);
	}

	@Test
	public void buildingFactoryReducesPlayerCredits() throws Exception {
		johnsHomePlanet.buildFactory(john);
		assertThat(john.getCredits(), is(Player.STARTING_CREDITS - Planet.FACTORY_COST));
	}

	@Test
	public void cannotBuildMoreFactoriesThanSlotsAvailable() throws Exception {
		int slots = johnsHomePlanet.getFactorySlotCount();

		for (int i = 0; i < slots; i++) {
			johnsHomePlanet.buildFactory(john);
		}
		try {
			johnsHomePlanet.buildFactory(john);
			fail("Expected exception");
		} catch (NoFactorySlotsAvailableException e) {
			// Expected behavior
		}
	}

}
