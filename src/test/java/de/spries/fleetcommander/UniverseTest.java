package de.spries.fleetcommander;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import de.spries.fleetcommander.Planet.NotPlayersOwnPlanetException;

public class UniverseTest {

	private Player john;
	private Player jack;
	private Planet johnsHomePlanet;
	private Planet jacksHomePlanet;
	private Planet uninhabitedPlanet;
	private Universe universe;

	@Before
	public void setUp() {
		john = mock(Player.class);
		jack = mock(Player.class);
		johnsHomePlanet = new Planet(1, 1, john);
		jacksHomePlanet = new Planet(5, 1, jack);
		uninhabitedPlanet = new Planet(3, 3);
		universe = new Universe(Arrays.asList(johnsHomePlanet, uninhabitedPlanet, jacksHomePlanet));
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
		assertThat(universe.getPlanets(), hasSize(3));
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void planetsMustHaveDifferentPosition() {
		new Universe(Arrays.asList(new Planet(0, 0), new Planet(0, 0)));
	}

	@Test
	public void onlyPlanetsInhabitedByPlayerAreReturned() throws Exception {
		assertThat(universe.getPlanetsInhabitedBy(john), Matchers.contains(johnsHomePlanet));
	}

	@Test
	public void onlyUninhabitedPlanetsAreReturned() throws Exception {
		assertThat(universe.getUninhabitedPlanets(), Matchers.contains(uninhabitedPlanet));
	}

	@Test
	public void sendingShipsReducedShipsCountOnOriginPlanet() throws Exception {
		int shipsBefore = johnsHomePlanet.getShipCount();
		universe.sendShips(1, johnsHomePlanet, uninhabitedPlanet, john);

		assertThat(johnsHomePlanet.getShipCount(), is(shipsBefore - 1));
	}

	@Test
	public void landingShipsIncreaseDestinationPlanetShipCount() throws Exception {
		universe.sendShips(1, johnsHomePlanet, uninhabitedPlanet, john);
		universe.runShipTravellingCycle();
		assertThat(uninhabitedPlanet.getShipCount(), is(1));
	}

	@Test
	public void landingShipsOnUninhabitedPlanetInhabitsPlanet() throws Exception {
		universe.sendShips(1, johnsHomePlanet, uninhabitedPlanet, john);
		universe.runShipTravellingCycle();
		assertThat(uninhabitedPlanet.isInhabitedBy(john), is(true));
	}

	@Test
	public void universeHasNoTravellingShips() throws Exception {
		assertThat(universe.getTravellingShipFormations(), hasSize(0));
	}

	@Test
	public void sendingShipsAddsTravellingShipsToUniverse() throws Exception {
		universe.sendShips(1, johnsHomePlanet, uninhabitedPlanet, john);
		List<ShipFormation> shipFormations = universe.getTravellingShipFormations();
		assertThat(shipFormations, hasSize(1));

		ShipFormation shipFormation = shipFormations.get(0);
		assertThat(shipFormation.getShipCount(), is(1));
		assertThat(shipFormation.getOrigin(), is(johnsHomePlanet));
		assertThat(shipFormation.getDestination(), is(uninhabitedPlanet));
		assertThat(shipFormation.getCommander(), is(john));
	}

	@Test
	public void sendingShipsToSameDestinationAgainIncreasesShipsTravelling() throws Exception {
		universe.sendShips(1, johnsHomePlanet, uninhabitedPlanet, john);
		universe.sendShips(2, johnsHomePlanet, uninhabitedPlanet, john);
		List<ShipFormation> shipFormations = universe.getTravellingShipFormations();
		assertThat(shipFormations, hasSize(1));

		ShipFormation shipFormation = shipFormations.get(0);
		assertThat(shipFormation.getShipCount(), is(2));
	}

	@Test(expected = NotPlayersOwnPlanetException.class)
	public void cannotSendShipsFromOtherPlayersPlanets() throws Exception {
		universe.sendShips(1, jacksHomePlanet, uninhabitedPlanet, john);
	}

	@Test(expected = NotPlayersOwnPlanetException.class)
	public void cannotSendShipsFromUninhabitedPlayersPlanets() throws Exception {
		universe.sendShips(1, uninhabitedPlanet, uninhabitedPlanet, john);
	}

	@Test
	public void sendingShipsToSamePlanetDoesntAffectUniverseOrPlanetShipCount() throws Exception {
		int shipsBefore = johnsHomePlanet.getShipCount();
		universe.sendShips(1, johnsHomePlanet, johnsHomePlanet, john);
		assertThat(johnsHomePlanet.getShipCount(), is(shipsBefore));
		assertThat(universe.getTravellingShipFormations(), is(empty()));
	}

	@Test(expected = NotEnoughShipsException.class)
	public void cannotSendMoreShipsThanLocatedOnOriginPlanet() throws Exception {
		int shipCount = johnsHomePlanet.getShipCount();
		universe.sendShips(shipCount + 1, johnsHomePlanet, uninhabitedPlanet, john);
	}

	@Test(expected = IllegalArgumentException.class)
	public void originMustBeInsideUniverse() throws Exception {
		universe.sendShips(1, mock(Planet.class), uninhabitedPlanet, john);
	}

	@Test(expected = IllegalArgumentException.class)
	public void destinationMustBeInsideUniverse() throws Exception {
		universe.sendShips(1, johnsHomePlanet, mock(Planet.class), john);
	}

	@Test
	public void test() {
		// TODO attack planet
		// TODO various distances
		fail("Not implemented");
	}
}
