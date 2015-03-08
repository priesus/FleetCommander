package de.spries.fleetcommander;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class PlanetTest {

	private static final Player JACK = new Player("Jack");
	private static final Player JOHN = new Player("John");

	@Test
	public void newPlanetHasCoordinates() {
		Planet p = new Planet(10, 20);
		assertThat(p.getCoordinateX(), is(10));
		assertThat(p.getCoordinateY(), is(20));
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

}
