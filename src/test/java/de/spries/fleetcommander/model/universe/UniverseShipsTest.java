package de.spries.fleetcommander.model.universe;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

import de.spries.fleetcommander.model.Player;

public class UniverseShipsTest {

	private static final int INEXISTENT_PLANET = 123456789;
	private Player john;
	private Planet johnsHomePlanet;
	private Planet uninhabitedPlanet;
	private Planet distantPlanet;
	private Universe universe;

	@Before
	public void setUp() {
		john = mock(Player.class);
		johnsHomePlanet = mock(Planet.class);
		uninhabitedPlanet = mock(Planet.class);
		distantPlanet = mock(Planet.class);
		universe = new Universe(Arrays.asList(johnsHomePlanet, uninhabitedPlanet, distantPlanet));
	}

	@Test
	public void sendingShipsAddsTravellingShipsToUniverse() throws Exception {
		universe.sendShips(1, johnsHomePlanet, uninhabitedPlanet, john);
		Collection<ShipFormation> shipFormations = universe.getTravellingShipFormations();
		assertThat(shipFormations, hasSize(1));

		assertThat(shipFormations, hasItem(new ShipFormation(1, johnsHomePlanet, uninhabitedPlanet, john)));
	}

	@Test
	public void sendingShipsAddsIncomingShipsToDestinatonPlanet() throws Exception {
		universe.sendShips(3, johnsHomePlanet, uninhabitedPlanet, john);
		verify(uninhabitedPlanet).addIncomingShips(3);
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
		universe.sendShips(1, johnsHomePlanet, distantPlanet, john);
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
	public void travellingToDistantPlanetTakesMultipleCycles() throws Exception {
		doReturn(15.0).when(johnsHomePlanet).distanceTo(distantPlanet);
		universe.sendShips(1, johnsHomePlanet, distantPlanet, john);

		universe.runShipTravellingCycle();
		assertThat(universe.getTravellingShipFormations(), hasSize(1));

		universe.runShipTravellingCycle();
		assertThat(universe.getTravellingShipFormations(), hasSize(0));
	}

	@Test
	public void shipsLandOnTargetPlanet() throws Exception {
		universe.sendShips(1, johnsHomePlanet, uninhabitedPlanet, john);
		universe.runShipTravellingCycle();
		verify(uninhabitedPlanet).landShips(1, john);
	}

	@Test
	public void planetsAreIdentifiedByIdForTravellingShips() throws Exception {
		doReturn(1).when(johnsHomePlanet).getId();
		doReturn(2).when(uninhabitedPlanet).getId();

		universe.sendShips(1, 1, 2, john);

		Collection<ShipFormation> shipFormations = universe.getTravellingShipFormations();
		assertThat(shipFormations, hasItem(new ShipFormation(1, johnsHomePlanet, uninhabitedPlanet, john)));
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
