package de.spries.fleetcommander.model.player;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import de.spries.fleetcommander.model.Game;
import de.spries.fleetcommander.model.universe.Planet;
import de.spries.fleetcommander.model.universe.Universe;

public class ComputerPlayerTest {

	private Player player;
	private Game game;
	private Planet homePlanet;

	@Before
	public void setUp() {
		player = new ComputerPlayer("Computer");

		game = mock(Game.class);
		Universe universe = mock(Universe.class);
		homePlanet = mock(Planet.class);

		doReturn(universe).when(game).getUniverse();
		doReturn(homePlanet).when(universe).getHomePlanetOf(player);
	}

	@Test
	public void endsTurnInNewTurn() {
		player.notifyNewTurn(game);
		verify(game).endTurn(player);
	}

	@Test
	public void buildsNoFactoriesWhenPlayerCannotBuild() throws Exception {
		doReturn(false).when(homePlanet).canBuildFactory(player);
		player.notifyNewTurn(game);
		verify(homePlanet, never()).buildFactory(player);
	}

	@Test
	public void buildsFactoriesWhenPlayerCanBuild() throws Exception {
		doReturn(true).doReturn(true).doReturn(false).when(homePlanet).canBuildFactory(player);
		player.notifyNewTurn(game);
		verify(homePlanet, times(2)).buildFactory(player);
	}

}
