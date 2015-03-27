package de.spries.fleetcommander.model.universe;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import de.spries.fleetcommander.model.player.Player;
import de.spries.fleetcommander.model.universe.Planet.NotPlayersOwnPlanetException;

public class PlanetShipsTest {

	private Player jack;
	private Player john;
	private Planet johnsHomePlanet;
	private Planet jacksHomePlanet;
	private Planet uninhabitedPlanet;
	private FactorySite johnsFactorySite;

	@Before
	public void setUp() {
		john = mock(Player.class);
		jack = mock(Player.class);

		johnsHomePlanet = new Planet(0, 0, john);
		jacksHomePlanet = new Planet(1, 1, jack);
		uninhabitedPlanet = new Planet(0, 0);

		johnsFactorySite = mock(FactorySite.class);
		johnsHomePlanet.setFactorySite(johnsFactorySite);
	}

	@Test
	public void homePlanetStartsWithShips() throws Exception {
		assertThat(johnsHomePlanet.getShipCount(), is(greaterThan(0)));
	}

	@Test
	public void uninhabitedPlanetStartsWithoutShips() throws Exception {
		assertThat(uninhabitedPlanet.getShipCount(), is(0));
	}

	@Test(expected = NotPlayersOwnPlanetException.class)
	public void cannotSendShipsFromOtherPlayersPlanets() throws Exception {
		jacksHomePlanet.sendShipsAway(1, john);
	}

	@Test(expected = NotPlayersOwnPlanetException.class)
	public void cannotSendShipsFromUninhabitedPlanets() throws Exception {
		uninhabitedPlanet.sendShipsAway(1, john);
	}

	@Test(expected = NotEnoughShipsException.class)
	public void cannotSendMoreShipsThanLocatedOnPlanet() throws Exception {
		int shipCount = johnsHomePlanet.getShipCount();
		johnsHomePlanet.sendShipsAway(shipCount + 1, john);
	}

	@Test
	public void sendingShipsReducedShipsCountOnPlanet() throws Exception {
		int shipsBefore = johnsHomePlanet.getShipCount();
		johnsHomePlanet.sendShipsAway(1, john);

		assertThat(johnsHomePlanet.getShipCount(), is(shipsBefore - 1));
	}

	@Test
	public void landingShipsIncreaseShipCount() throws Exception {
		int shipsBefore = johnsHomePlanet.getShipCount();
		johnsHomePlanet.landShips(1, john);
		assertThat(johnsHomePlanet.getShipCount(), is(shipsBefore + 1));
	}

	@Test
	public void landingShipsOnUninhabitedPlanetInhabitsPlanet() throws Exception {
		uninhabitedPlanet.landShips(1, john);
		assertThat(uninhabitedPlanet.isInhabitedBy(john), is(true));
		assertThat(uninhabitedPlanet.getShipCount(), is(1));
	}

	@Test
	public void landingZeroShipsDoesNotInhabitPlanet() throws Exception {
		uninhabitedPlanet.landShips(0, john);
		assertThat(uninhabitedPlanet.isInhabitedBy(john), is(false));
		assertThat(uninhabitedPlanet.getShipCount(), is(0));
	}

	@Test
	public void invadedPlanetIsNotHomePlanet() throws Exception {
		uninhabitedPlanet.landShips(1, john);
		assertThat(uninhabitedPlanet.isHomePlanetOf(john), is(false));
	}
}
