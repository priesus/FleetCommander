package de.spries.fleetcommander.model.ai;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import de.spries.fleetcommander.model.facade.PlayerSpecificPlanet;
import de.spries.fleetcommander.model.facade.PlayerSpecificUniverse;

public class DefaultBuildingStrategyTest {

	private PlayerSpecificUniverse universe;
	private BuildingStrategy buildingStrategy;
	private PlayerSpecificPlanet homePlanet;
	private PlayerSpecificPlanet closePlanet;
	private PlayerSpecificPlanet distantPlanet;

	@Before
	public void setUp() throws Exception {
		buildingStrategy = new DefaultBuildingStrategy();
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

		doReturn(true).when(homePlanet).isInhabitedByMe();
		doReturn(true).when(closePlanet).isInhabitedByMe();
		doReturn(true).when(distantPlanet).isInhabitedByMe();
	}

	@Test
	public void buildsNoFactoriesWhenPlayerCannotBuild() throws Exception {
		doReturn(false).when(homePlanet).canBuildFactory();
		buildingStrategy.buildFactories(universe);
		verify(homePlanet, never()).buildFactory();
	}

	@Test
	public void buildsFactoriesWhenPlayerCanBuild() throws Exception {
		doReturn(true).doReturn(true).doReturn(false).when(homePlanet).canBuildFactory();
		buildingStrategy.buildFactories(universe);
		verify(homePlanet, times(2)).buildFactory();
	}

	@Test
	public void buildsFactoriesOnPlanetsCloseToHomePlanetFirst() {
		doReturn(true).doReturn(false).when(homePlanet).canBuildFactory();
		doReturn(true).doReturn(false).when(closePlanet).canBuildFactory();
		doReturn(true).doReturn(false).when(distantPlanet).canBuildFactory();

		buildingStrategy.buildFactories(universe);

		InOrder inOrder = Mockito.inOrder(homePlanet, closePlanet, distantPlanet);
		inOrder.verify(homePlanet).buildFactory();
		inOrder.verify(closePlanet).buildFactory();
		inOrder.verify(distantPlanet).buildFactory();
	}

}
