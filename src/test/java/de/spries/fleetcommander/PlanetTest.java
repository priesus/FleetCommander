package de.spries.fleetcommander;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import de.spries.fleetcommander.Planet.NoFactorySlotsAvailableException;
import de.spries.fleetcommander.Planet.NotPlayersOwnPlanetException;

public class PlanetTest {

	private Player jack;
	private Player john;
	private Planet johnsHomePlanet;
	private Planet jacksHomePlanet;
	private Planet uninhabitedPlanet;

	@Before
	public void setUp() {
		john = mock(Player.class);
		jack = mock(Player.class);

		johnsHomePlanet = new Planet(0, 0, john);
		jacksHomePlanet = new Planet(1, 1, jack);
		uninhabitedPlanet = new Planet(0, 0);
	}

	@Test
	public void newPlanetHasCoordinates() {
		Planet planet = new Planet(10, 20);
		assertThat(planet.getCoordinateX(), is(10));
		assertThat(planet.getCoordinateY(), is(20));
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

	@Test
	public void homePlanetStartsWithShips() throws Exception {
		assertThat(johnsHomePlanet.getShipCount(), is(Planet.HOME_PLANET_STARTING_SHIPS));
	}

	@Test
	public void uninhabitedPlanetStartsWithoutShips() throws Exception {
		assertThat(uninhabitedPlanet.getShipCount(), is(0));
	}

	@Test
	public void factoryCycleInreasesNumberOfShips() throws Exception {
		int shipsBefore = johnsHomePlanet.getShipCount();
		johnsHomePlanet.buildFactory(john);
		johnsHomePlanet.runFactoryCycle();

		assertThat(johnsHomePlanet.getShipCount(), is(shipsBefore + Planet.SHIPS_PER_FACTORY_PER_TURN));
	}

	@Test
	public void noShipsAreBuildWithoutFactories() throws Exception {
		int shipsBefore = johnsHomePlanet.getShipCount();
		johnsHomePlanet.runFactoryCycle();

		assertThat(johnsHomePlanet.getShipCount(), is(shipsBefore));
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
		verify(john).reduceCredits(Mockito.eq(Planet.FACTORY_COST));
	}

	@Test
	public void cannotBuildMoreFactoriesThanSlotsAvailable() throws Exception {
		john.addCredits(500);
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

	@Test
	public void factoryCountIncreasesWithEachBuiltFactory() throws Exception {
		john.addCredits(500);
		int slots = johnsHomePlanet.getFactorySlotCount();

		assertThat(johnsHomePlanet.getFactoryCount(), is(0));

		for (int i = 0; i < slots; i++) {
			johnsHomePlanet.buildFactory(john);
			assertThat(johnsHomePlanet.getFactoryCount(), is(i + 1));
		}
	}

	@Test
	public void eachFactoryIncreasesCreditsProduction() throws Exception {
		assertThat(johnsHomePlanet.getProducedCreditsPerTurn(), is(0));
		for (int i = 0; i < 5; i++) {
			johnsHomePlanet.buildFactory(john);
			assertThat(johnsHomePlanet.getProducedCreditsPerTurn(), is((i + 1) * Planet.CREDITS_PER_FACTORY_PER_TURN));
		}
	}

	@Test
	public void eachFactoryIncreasesShipProduction() throws Exception {
		assertThat(johnsHomePlanet.getProducedShipsPerTurn(), is(0));
		for (int i = 0; i < 5; i++) {
			johnsHomePlanet.buildFactory(john);
			assertThat(johnsHomePlanet.getProducedShipsPerTurn(), is((i + 1) * Planet.SHIPS_PER_FACTORY_PER_TURN));
		}
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
}
