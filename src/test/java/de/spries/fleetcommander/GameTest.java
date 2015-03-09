package de.spries.fleetcommander;

import org.junit.Test;

import de.spries.fleetcommander.Game.NotEnoughPlayersException;

public class GameTest {

	@Test(expected = NotEnoughPlayersException.class)
	public void gameRequiresAtLeastOnePlayerToStart() throws Exception {
		new Game().start();
	}

	@Test(expected = IllegalStateException.class)
	public void gameRequiresAUniverseToStart() throws Exception {
		Game game = new Game();
		game.createHumanPlayer("John");
		game.start();
	}
}
