package de.spries.fleetcommander;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import de.spries.fleetcommander.Planet.NotPlayersOwnPlanetException;

public class PlanetTest {

	private static final Player JACK = new Player("Jack");
	private static final Player JOHN = new Player("John");

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
		Planet homePlanet = new Planet(0, 0, JOHN);
		Planet planet = new Planet(0, 0);

		assertThat(homePlanet.isHomePlanetOf(JOHN), is(true));
		assertThat(homePlanet.isHomePlanetOf(JACK), is(false));

		assertThat(planet.isHomePlanetOf(JOHN), is(false));
		assertThat(planet.isHomePlanetOf(JACK), is(false));
	}

	@Test
	public void homePlanetIsOwnedByInhabitingPlayerOnly() throws Exception {
		Planet homePlanet = new Planet(0, 0, JOHN);

		assertThat(homePlanet.isInhabitedBy(JOHN), is(true));
		assertThat(homePlanet.isInhabitedBy(JACK), is(false));
	}

	@Test(expected = NotPlayersOwnPlanetException.class)
	public void cannotBuildFactoryOnUninhabitedPlanet() throws Exception {
		Planet planet = new Planet(0, 0);
		planet.buildFactory(JOHN);
	}

	@Test(expected = NotPlayersOwnPlanetException.class)
	public void cannotBuildFactoryOnOtherPlayersPlanet() throws Exception {
		Planet planet = new Planet(0, 0, JACK);
		planet.buildFactory(JOHN);
	}

	@Test
	public void buildingFactoryReducesPlayerCredits() throws Exception {
		Planet planet = new Planet(0, 0, JACK);
		planet.buildFactory(JACK);
		assertThat(JACK.getCredits(), is(Player.STARTING_BALANCE - Planet.FACTORY_COST));
	}

}
