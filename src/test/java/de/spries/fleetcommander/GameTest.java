package de.spries.fleetcommander;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import de.spries.fleetcommander.Game.NotEnoughPlayersException;

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
	public void endingTurnRunsFactoryCycle() throws Exception {
		startedGame.endTurn();
		verify(universe).runFactoryCycle();
	}

	@Test
	public void endingTurnRunsShipTravellingCycle() throws Exception {
		startedGame.endTurn();
		verify(universe).runShipTravellingCycle();
	}

}
