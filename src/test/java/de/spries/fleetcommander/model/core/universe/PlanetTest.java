package de.spries.fleetcommander.model.core.universe;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import de.spries.fleetcommander.model.core.Player;
import de.spries.fleetcommander.model.core.universe.FactorySite;
import de.spries.fleetcommander.model.core.universe.Planet;

public class PlanetTest {

	private Player jack;
	private Player john;
	private Planet johnsHomePlanet;
	private Planet uninhabitedPlanet;
	private FactorySite johnsFactorySite;

	@Before
	public void setUp() {
		john = mock(Player.class);
		jack = mock(Player.class);

		johnsHomePlanet = new Planet(0, 0, john);
		uninhabitedPlanet = new Planet(0, 0);

		johnsFactorySite = mock(FactorySite.class);
		johnsHomePlanet.setFactorySite(johnsFactorySite);
	}

	@Test
	public void newPlanetHasCoordinates() {
		Planet planet = new Planet(10, 20);
		assertThat(planet.getX(), is(10));
		assertThat(planet.getY(), is(20));
	}

	@Test
	public void planetsWithInhabitantAreInhabited() throws Exception {
		assertThat(uninhabitedPlanet.isInhabited(), is(false));
		assertThat(johnsHomePlanet.isInhabited(), is(true));
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
}
