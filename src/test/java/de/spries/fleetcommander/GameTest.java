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
		Game g = new Game();
		g.createHumanPlayer("John");
		g.start();
	}
}
