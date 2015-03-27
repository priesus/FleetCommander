package de.spries.fleetcommander.model;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import de.spries.fleetcommander.model.Game.NotEnoughPlayersException;
import de.spries.fleetcommander.model.player.Player;
import de.spries.fleetcommander.model.universe.Universe;

public class GameTest {

	private Game game;
	private Game startedGame;
	private Universe universe;

	@Before
	public void setUp() throws Exception {
		game = new Game();
		startedGame = new Game();
		startedGame.createHumanPlayer("John");
		universe = mock(Universe.class);
		startedGame.setUniverse(universe);
		startedGame.start();
	}

	@Test(expected = NotEnoughPlayersException.class)
	public void gameRequiresAtLeastOnePlayerToStart() throws Exception {
		game.start();
	}

	@Test(expected = IllegalStateException.class)
	public void gameRequiresAUniverseToStart() throws Exception {
		game.createHumanPlayer("John");
		game.start();
	}

	@Test
	public void playerIsAddedToPlayersList() throws Exception {
		game.createHumanPlayer("John");
		assertThat(game.getPlayers(), hasItem(new Player("John")));
	}

	@Test
	public void endingTurnRunsFactoryCycle() throws Exception {
		startedGame.endTurn();
		verify(universe).runFactoryProductionCycle();
	}

	@Test
	public void endingTurnRunsShipTravellingCycle() throws Exception {
		startedGame.endTurn();
		verify(universe).runShipTravellingCycle();
	}

}
