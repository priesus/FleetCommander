package de.spries.fleetcommander.model.ai.behavior;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.spries.fleetcommander.model.ai.behavior.AggressiveFleetStrategy;
import de.spries.fleetcommander.model.ai.behavior.FleetStrategy;
import de.spries.fleetcommander.model.facade.PlayerSpecificPlanet;
import de.spries.fleetcommander.model.facade.PlayerSpecificUniverse;

public class AggressiveFleetStrategyTest {

	private PlayerSpecificUniverse universe;
	private FleetStrategy fleetStrategy;
	private PlayerSpecificPlanet homePlanet;
	private PlayerSpecificPlanet closePlanet;
	private PlayerSpecificPlanet distantPlanet;

	@Before
	public void setUp() throws Exception {
		fleetStrategy = new AggressiveFleetStrategy();
		universe = mock(PlayerSpecificUniverse.class);
		homePlanet = mock(PlayerSpecificPlanet.class);
		closePlanet = mock(PlayerSpecificPlanet.class);
		distantPlanet = mock(PlayerSpecificPlanet.class);

		doReturn(homePlanet).when(universe).getHomePlanet();
		List<PlayerSpecificPlanet> planets = Arrays.asList(closePlanet, distantPlanet, homePlanet);
		doReturn(planets).when(universe).getPlanets();

		doReturn(0.).when(homePlanet).distanceTo(homePlanet);
		doReturn(1.).when(closePlanet).distanceTo(homePlanet);
		doReturn(2.).when(distantPlanet).distanceTo(homePlanet);

		doReturn(1).when(homePlanet).getId();
		doReturn(2).when(closePlanet).getId();
		doReturn(3).when(distantPlanet).getId();

		doReturn(true).when(homePlanet).isInhabitedByMe();
		doReturn(false).when(homePlanet).isKnownAsEnemyPlanet();
	}

	@Test
	public void sendsAvailableShipToClosestPlanets() {
		doReturn(1).when(homePlanet).getShipCount();
		doReturn(false).when(closePlanet).isInhabitedByMe();
		doReturn(false).when(distantPlanet).isInhabitedByMe();
		doReturn(false).when(closePlanet).isKnownAsEnemyPlanet();
		doReturn(false).when(distantPlanet).isKnownAsEnemyPlanet();
		doReturn(0).when(closePlanet).getIncomingShipCount();
		doReturn(0).when(distantPlanet).getIncomingShipCount();

		fleetStrategy.sendShips(universe);

		verify(universe).sendShips(1, 1, 2);
	}

	@Test
	public void sendsAvailableShipToClosestUninhabitedPlanets() {
		doReturn(1).when(homePlanet).getShipCount();
		doReturn(true).when(closePlanet).isInhabitedByMe();
		doReturn(false).when(distantPlanet).isInhabitedByMe();
		doReturn(false).when(closePlanet).isKnownAsEnemyPlanet();
		doReturn(false).when(distantPlanet).isKnownAsEnemyPlanet();
		doReturn(0).when(closePlanet).getIncomingShipCount();
		doReturn(0).when(distantPlanet).getIncomingShipCount();

		fleetStrategy.sendShips(universe);

		verify(universe).sendShips(1, 1, 3);
	}

	@Test
	public void sendsOnlyOneShipToUninhabitedPlanets() {
		doReturn(2).when(homePlanet).getShipCount();
		doReturn(false).when(closePlanet).isInhabitedByMe();
		doReturn(false).when(distantPlanet).isInhabitedByMe();
		doReturn(false).when(closePlanet).isKnownAsEnemyPlanet();
		doReturn(false).when(distantPlanet).isKnownAsEnemyPlanet();
		doReturn(0).when(closePlanet).getIncomingShipCount();
		doReturn(0).when(distantPlanet).getIncomingShipCount();

		fleetStrategy.sendShips(universe);

		verify(universe).sendShips(1, 1, 2);
		verify(universe).sendShips(1, 1, 3);
	}

	@Test
	public void sendsOnlyOneShipToPlanetsWithoutIncomingShips() {
		doReturn(2).when(homePlanet).getShipCount();
		doReturn(false).when(closePlanet).isInhabitedByMe();
		doReturn(false).when(distantPlanet).isInhabitedByMe();
		doReturn(false).when(closePlanet).isKnownAsEnemyPlanet();
		doReturn(false).when(distantPlanet).isKnownAsEnemyPlanet();
		doReturn(1).when(closePlanet).getIncomingShipCount();
		doReturn(0).when(distantPlanet).getIncomingShipCount();

		fleetStrategy.sendShips(universe);

		verify(universe).sendShips(1, 1, 3);
	}

	@Test
	public void sendsAllShipsToEnemyPlanetIfOneIsKnown() throws Exception {
		doReturn(2).when(homePlanet).getShipCount();
		doReturn(3).when(closePlanet).getShipCount();
		doReturn(true).when(closePlanet).isInhabitedByMe();
		doReturn(false).when(distantPlanet).isInhabitedByMe();
		doReturn(false).when(closePlanet).isKnownAsEnemyPlanet();
		doReturn(true).when(distantPlanet).isKnownAsEnemyPlanet();

		fleetStrategy.sendShips(universe);

		verify(universe).sendShips(2, 1, 3);
		verify(universe).sendShips(3, 2, 3);
	}

}
