package de.spries.fleetcommander.model.core.universe;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

import de.spries.fleetcommander.model.core.Player;
import de.spries.fleetcommander.model.core.common.IllegalActionException;

public class UniverseTest {

	private static final int INEXISTENT_PLANET = 123456789;
	private Player john;
	private Player jack;
	private Planet johnsHomePlanet;
	private Planet jacksHomePlanet;
	private Planet uninhabitedPlanet;
	private Planet distantPlanet;
	private Universe universe;

	@Before
	public void setUp() {
		john = mock(Player.class);
		jack = mock(Player.class);
		johnsHomePlanet = mock(Planet.class);
		jacksHomePlanet = mock(Planet.class);
		uninhabitedPlanet = mock(Planet.class);
		distantPlanet = mock(Planet.class);
		doReturn(true).when(johnsHomePlanet).isHomePlanet();
		doReturn(true).when(jacksHomePlanet).isHomePlanet();
		universe = new Universe(Arrays.asList(johnsHomePlanet, jacksHomePlanet, uninhabitedPlanet, distantPlanet));
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
		assertThat(universe.getPlanets(), is(not(empty())));
	}

	@Test
	public void universeHasHomePlanets() throws Exception {
		Collection<Planet> homePlanets = universe.getHomePlanets();
		assertThat(homePlanets, hasItem(johnsHomePlanet));
		assertThat(homePlanets, hasItem(jacksHomePlanet));
		assertThat(homePlanets, hasSize(2));
	}

	@Test
	public void universeHasNoTravellingShips() throws Exception {
		assertThat(universe.getTravellingShipFormations(), hasSize(0));
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
		verify(uninhabitedPlanet).addIncomingShips(3, john);
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

	@Test(expected = IllegalActionException.class)
	public void originMustBeInsideUniverse() throws Exception {
		universe.sendShips(1, mock(Planet.class), uninhabitedPlanet, john);
	}

	@Test(expected = IllegalActionException.class)
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
	public void runsFactoryProductionCycleOnEveryPlanet() throws Exception {
		universe.runFactoryProductionCycle();
		verify(johnsHomePlanet).runProductionCycle();
		verify(jacksHomePlanet).runProductionCycle();
		verify(uninhabitedPlanet).runProductionCycle();
		verify(distantPlanet).runProductionCycle();
	}

	@Test
	public void resetsPreviousTurnMarkersOnEveryPlanet() throws Exception {
		universe.resetPreviousTurnMarkers();
		verify(johnsHomePlanet).resetMarkers();
		verify(jacksHomePlanet).resetMarkers();
		verify(uninhabitedPlanet).resetMarkers();
		verify(distantPlanet).resetMarkers();
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

	@Test
	public void setsEventBusForAllContainedPlanets() throws Exception {
		TurnEventBus eventBus = mock(TurnEventBus.class);
		universe.setEventBus(eventBus);

		verify(johnsHomePlanet).setEventBus(eventBus);
		verify(jacksHomePlanet).setEventBus(eventBus);
		verify(uninhabitedPlanet).setEventBus(eventBus);
		verify(distantPlanet).setEventBus(eventBus);
	}

	@Test
	public void forwardsHandleDefeatedPlayerToAllPlanets() throws Exception {
		universe.handleDefeatedPlayer(john);

		verify(johnsHomePlanet).handleDefeatedPlayer(john);
		verify(jacksHomePlanet).handleDefeatedPlayer(john);
		verify(uninhabitedPlanet).handleDefeatedPlayer(john);
		verify(distantPlanet).handleDefeatedPlayer(john);
	}

	@Test
	public void removesDefeatedPlayersTravellingShips() throws Exception {
		universe.sendShips(1, johnsHomePlanet, uninhabitedPlanet, john);
		universe.sendShips(1, jacksHomePlanet, uninhabitedPlanet, jack);
		universe.handleDefeatedPlayer(john);

		Collection<ShipFormation> shipFormations = universe.getTravellingShipFormations();
		assertThat(shipFormations, hasSize(1));
		assertThat(shipFormations, hasItem(new ShipFormation(1, jacksHomePlanet, uninhabitedPlanet, jack)));
	}
}
