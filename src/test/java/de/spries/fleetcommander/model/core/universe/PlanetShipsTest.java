package de.spries.fleetcommander.model.core.universe;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import de.spries.fleetcommander.model.core.Player;
import de.spries.fleetcommander.model.core.common.IllegalActionException;
import de.spries.fleetcommander.model.core.universe.FactorySite;
import de.spries.fleetcommander.model.core.universe.Planet;

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

	@Test(expected = IllegalActionException.class)
	public void cannotSendShipsFromOtherPlayersPlanets() throws Exception {
		jacksHomePlanet.sendShipsAway(1, john);
	}

	@Test(expected = IllegalActionException.class)
	public void cannotSendShipsFromUninhabitedPlanets() throws Exception {
		uninhabitedPlanet.sendShipsAway(1, john);
	}

	@Test(expected = IllegalActionException.class)
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

	@Test(expected = IllegalArgumentException.class)
	public void landingZeroShipsDoesNotInhabitPlanet() throws Exception {
		uninhabitedPlanet.landShips(0, john);
	}

	@Test
	public void invadedPlanetIsNotHomePlanet() throws Exception {
		uninhabitedPlanet.landShips(1, john);
		assertThat(uninhabitedPlanet.isHomePlanetOf(john), is(false));
	}

	@Test
	public void planetHasNoShipsIncomingInitially() throws Exception {
		assertThat(uninhabitedPlanet.getIncomingShipCount(), is(0));
	}

	@Test
	public void addingIncomingShipsIncreasesIncomingShips() throws Exception {
		uninhabitedPlanet.addIncomingShips(1);
		assertThat(uninhabitedPlanet.getIncomingShipCount(), is(1));
		uninhabitedPlanet.addIncomingShips(2);
		assertThat(uninhabitedPlanet.getIncomingShipCount(), is(3));
	}

	@Test
	public void landingShipsReducesIncomingShips() throws Exception {
		uninhabitedPlanet.addIncomingShips(2);

		uninhabitedPlanet.landShips(1, john);
		assertThat(uninhabitedPlanet.getIncomingShipCount(), is(1));

		uninhabitedPlanet.landShips(1, john);
		assertThat(uninhabitedPlanet.getIncomingShipCount(), is(0));
	}

	@Test
	public void attackingWithSomeShipsReducesEnemyShips() throws Exception {
		jacksHomePlanet.landShips(1, john);
		assertThat(jacksHomePlanet.getShipCount(), is(5));
		assertThat(jacksHomePlanet.getInhabitant(), is(jack));

		jacksHomePlanet.landShips(2, john);
		assertThat(jacksHomePlanet.getShipCount(), is(3));
		assertThat(jacksHomePlanet.getInhabitant(), is(jack));
	}

	@Test
	public void attackingWithEqualNumberOfShipsDestroysAllEnemyShips() throws Exception {
		jacksHomePlanet.landShips(6, john);
		assertThat(jacksHomePlanet.getShipCount(), is(0));
		assertThat(jacksHomePlanet.getInhabitant(), is(jack));
	}

	@Test
	public void attackingWithMoreShipsInhabitsEnemyPlanet() throws Exception {
		jacksHomePlanet.landShips(7, john);
		assertThat(jacksHomePlanet.getShipCount(), is(1));
		assertThat(jacksHomePlanet.getInhabitant(), is(john));
	}
}
