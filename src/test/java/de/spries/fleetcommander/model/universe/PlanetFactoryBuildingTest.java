package de.spries.fleetcommander.model.universe;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import de.spries.fleetcommander.model.common.IllegalActionException;
import de.spries.fleetcommander.model.player.Player;
import de.spries.fleetcommander.model.player.Player.InsufficientCreditsException;
import de.spries.fleetcommander.model.universe.FactorySite.NoFactorySlotsAvailableException;

public class PlanetFactoryBuildingTest {

	private static final int SUFFICIENT_CREDITS = FactorySite.FACTORY_COST;
	private static final int INSUFFICIENT_CREDITS = SUFFICIENT_CREDITS - 1;
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
	public void factoryCycleInreasesNumberOfShips() throws Exception {
		int shipsBefore = johnsHomePlanet.getShipCount();
		doReturn(5f).when(johnsFactorySite).getProducedShipsPerTurn();
		johnsHomePlanet.runProductionCycle();

		assertThat(johnsHomePlanet.getShipCount(), is(shipsBefore + 5));
	}

	@Test
	public void lowShipProductionRequiresMultipleCyclesToProduceOneShip() throws Exception {
		int shipsBefore = johnsHomePlanet.getShipCount();
		doReturn(0.35f).when(johnsFactorySite).getProducedShipsPerTurn();

		johnsHomePlanet.runProductionCycle();
		assertThat(johnsHomePlanet.getShipCount(), is(shipsBefore));

		johnsHomePlanet.runProductionCycle();
		assertThat(johnsHomePlanet.getShipCount(), is(shipsBefore));

		johnsHomePlanet.runProductionCycle();
		assertThat(johnsHomePlanet.getShipCount(), is(shipsBefore + 1));
	}

	@Test(expected = IllegalActionException.class)
	public void buildFactoryOnUninhabitedPlanetThrowsException() throws Exception {
		uninhabitedPlanet.buildFactory(john);
	}

	@Test(expected = IllegalActionException.class)
	public void buildFactoryOnOtherPlayersPlanetThrowsException() throws Exception {
		johnsHomePlanet.buildFactory(jack);
	}

	@Test
	public void buildingFactoryReducesPlayerCredits() throws Exception {
		doReturn(SUFFICIENT_CREDITS).when(john).getCredits();
		doReturn(true).when(johnsFactorySite).hasAvailableSlots();

		johnsHomePlanet.buildFactory(john);
		verify(john).reduceCredits(Mockito.eq(SUFFICIENT_CREDITS));
	}

	@Test
	public void noCreditsRemovedWhenNoFactorySlotsAvailable() throws Exception {
		doReturn(false).when(johnsFactorySite).hasAvailableSlots();
		doThrow(NoFactorySlotsAvailableException.class).when(johnsFactorySite).buildFactory();

		try {
			johnsHomePlanet.buildFactory(john);
			fail("Expected exception");
		} catch (IllegalActionException e) {
			// expected behavior
		}
		verify(john, never()).reduceCredits(Mockito.anyInt());
	}

	@Test
	public void cannotBuildFactoryWithInsufficientCredits_() throws Exception {
		doReturn(true).when(johnsFactorySite).hasAvailableSlots();
		doThrow(InsufficientCreditsException.class).when(john).reduceCredits(Mockito.anyInt());

		try {
			johnsHomePlanet.buildFactory(john);
			fail("Expected exception");
		} catch (IllegalActionException e) {
			// expected behavior
		}
		verify(johnsFactorySite, never()).buildFactory();
	}

	@Test
	public void cannotBuildFactoryWithInsufficientCredits() throws Exception {
		doReturn(INSUFFICIENT_CREDITS).when(john).getCredits();
		doReturn(true).when(johnsFactorySite).hasAvailableSlots();

		assertThat(johnsHomePlanet.canBuildFactory(john), is(false));
		verify(john).getCredits();
	}

	@Test
	public void cannotBuildFactoryWithoutAvailableSlots() throws Exception {
		doReturn(SUFFICIENT_CREDITS).when(john).getCredits();
		doReturn(false).when(johnsFactorySite).hasAvailableSlots();

		assertThat(johnsHomePlanet.canBuildFactory(john), is(false));
		verify(johnsFactorySite).hasAvailableSlots();
	}

	@Test
	public void cannotBuildFactoryOnOtherPlayersPlanet() throws Exception {
		doReturn(SUFFICIENT_CREDITS).when(john).getCredits();
		doReturn(true).when(johnsFactorySite).hasAvailableSlots();
		assertThat(johnsHomePlanet.canBuildFactory(jack), is(false));
	}

	@Test
	public void cannotBuildFactoryOnUninhabitedPlanet() throws Exception {
		doReturn(SUFFICIENT_CREDITS).when(john).getCredits();
		doReturn(true).when(johnsFactorySite).hasAvailableSlots();
		assertThat(uninhabitedPlanet.canBuildFactory(john), is(false));
	}

	@Test
	public void canBuildFactory() throws Exception {
		doReturn(SUFFICIENT_CREDITS).when(john).getCredits();
		doReturn(true).when(johnsFactorySite).hasAvailableSlots();
		assertThat(johnsHomePlanet.canBuildFactory(john), is(true));
	}
}
