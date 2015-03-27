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

import de.spries.fleetcommander.model.player.Player;
import de.spries.fleetcommander.model.player.Player.InsufficientCreditsException;
import de.spries.fleetcommander.model.universe.FactorySite.NoFactorySlotsAvailableException;
import de.spries.fleetcommander.model.universe.Planet.NotPlayersOwnPlanetException;

public class PlanetFactoryBuildingTest {

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
		doReturn(5).when(johnsFactorySite).getProducedShipsPerTurn();
		johnsHomePlanet.runProductionCycle();

		assertThat(johnsHomePlanet.getShipCount(), is(shipsBefore + 5));
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
		doReturn(true).when(johnsFactorySite).hasAvailableSlots();

		johnsHomePlanet.buildFactory(john);
		verify(john).reduceCredits(Mockito.eq(FactorySite.FACTORY_COST));
	}

	@Test
	public void noCreditsRemovedWhenNoFactorySlotsAvailable() throws Exception {
		doReturn(false).when(johnsFactorySite).hasAvailableSlots();
		doThrow(NoFactorySlotsAvailableException.class).when(johnsFactorySite).buildFactory();

		try {
			johnsHomePlanet.buildFactory(john);
			fail("Expected exception");
		} catch (NoFactorySlotsAvailableException e) {
			// expected behavior
		}
		verify(john, never()).reduceCredits(Mockito.anyInt());
	}

	@Test
	public void cannotBuildMoreFactoriesPlayerCannotAffort() throws Exception {
		doReturn(true).when(johnsFactorySite).hasAvailableSlots();
		doThrow(InsufficientCreditsException.class).when(john).reduceCredits(Mockito.anyInt());

		try {
			johnsHomePlanet.buildFactory(john);
			fail("Expected exception");
		} catch (InsufficientCreditsException e) {
			// expected behavior
		}
		verify(johnsFactorySite, never()).buildFactory();
	}
}
