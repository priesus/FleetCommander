package de.spries.fleetcommander.model.universe;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

import de.spries.fleetcommander.model.Player;

public class UniverseShipsTest {

	private static final int INEXISTENT_PLANET = 123456789;
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
		johnsHomePlanet.setId(0);
		jacksHomePlanet.setId(1);
		uninhabitedPlanet.setId(2);
		universe = new Universe(Arrays.asList(johnsHomePlanet, uninhabitedPlanet, jacksHomePlanet));
	}

	@Test
	public void sendingShipsAddsTravellingShipsToUniverse() throws Exception {
		universe.sendShips(1, johnsHomePlanet, uninhabitedPlanet, john);
		Collection<ShipFormation> shipFormations = universe.getTravellingShipFormations();
		assertThat(shipFormations, hasSize(1));

		assertThat(shipFormations, hasItem(new ShipFormation(1, johnsHomePlanet, uninhabitedPlanet, john)));
	}

	@Test
	public void planetHasNoShipsIncoming() throws Exception {
		assertThat(uninhabitedPlanet.getIncomingShipCount(), is(0));
	}

	@Test
	public void sendingShipsAddsIncomingShipsToDestinatopnPlanet() throws Exception {
		universe.sendShips(1, johnsHomePlanet, uninhabitedPlanet, john);
		assertThat(uninhabitedPlanet.getIncomingShipCount(), is(1));
	}

	@Test
	public void sendingMoreShipsAddsIncomingShipsToDestinatonPlanet() throws Exception {
		universe.sendShips(1, johnsHomePlanet, uninhabitedPlanet, john);
		universe.sendShips(2, johnsHomePlanet, uninhabitedPlanet, john);
		assertThat(uninhabitedPlanet.getIncomingShipCount(), is(3));
	}

	@Test
	public void runningTravellingCycleRemovesIncomingShips() throws Exception {
		universe.sendShips(1, johnsHomePlanet, uninhabitedPlanet, john);
		universe.runShipTravellingCycle();
		assertThat(uninhabitedPlanet.getIncomingShipCount(), is(0));
	}

	@Test
	public void sendingShipsToSameDestinationAgainIncreasesShipsTravelling() throws Exception {
		universe.sendShips(1, johnsHomePlanet, uninhabitedPlanet, john);
		universe.sendShips(2, johnsHomePlanet, uninhabitedPlanet, john);
		Collection<ShipFormation> shipFormations = universe.getTravellingShipFormations();
		assertThat(shipFormations, hasSize(1));

		assertThat(shipFormations, hasItem(new ShipFormation(3, johnsHomePlanet, uninhabitedPlanet, john)));
	}

	@Test
	public void sendingShipsToDifferentDestinationAddsAnotherShipFormation() throws Exception {
		universe.sendShips(1, johnsHomePlanet, uninhabitedPlanet, john);
		universe.sendShips(1, johnsHomePlanet, jacksHomePlanet, john);
		Collection<ShipFormation> shipFormations = universe.getTravellingShipFormations();
		assertThat(shipFormations, hasSize(2));
	}

	@Test
	public void sendingShipsToSamePlanetDoesntAffectUniverseOrPlanetShipCount() throws Exception {
		int shipsBefore = johnsHomePlanet.getShipCount();
		universe.sendShips(1, johnsHomePlanet, johnsHomePlanet, john);
		assertThat(johnsHomePlanet.getShipCount(), is(shipsBefore));
		assertThat(universe.getTravellingShipFormations(), is(empty()));
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
	public void runningTravellingCycleRemovedShipsFromSpace() throws Exception {
		universe.sendShips(1, johnsHomePlanet, uninhabitedPlanet, john);
		universe.runShipTravellingCycle();
		assertThat(universe.getTravellingShipFormations(), is(empty()));
	}

	@Test
	public void shipsLandOnTargetPlanet() throws Exception {
		universe.sendShips(1, johnsHomePlanet, uninhabitedPlanet, john);
		universe.runShipTravellingCycle();
		assertThat(uninhabitedPlanet.isInhabitedBy(john), is(true));
		assertThat(uninhabitedPlanet.getShipCount(), is(1));
	}

	@Test
	public void planetsAreIdentifiedByIdForTravellingShips() throws Exception {
		universe.sendShips(1, johnsHomePlanet.getId(), uninhabitedPlanet.getId(), john);
		universe.runShipTravellingCycle();
		assertThat(uninhabitedPlanet.isInhabitedBy(john), is(true));
		assertThat(uninhabitedPlanet.getShipCount(), is(1));
	}

	@Test(expected = NoSuchElementException.class)
	public void originPlanetIsInvalidId() throws Exception {
		universe.sendShips(1, INEXISTENT_PLANET, uninhabitedPlanet.getId(), john);
	}

	@Test(expected = NoSuchElementException.class)
	public void destinationPlanetIsInvalidId() throws Exception {
		universe.sendShips(1, johnsHomePlanet.getId(), INEXISTENT_PLANET, john);
	}
}
