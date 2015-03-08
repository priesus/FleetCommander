package de.spries.fleetcommander;

import java.util.Arrays;

import org.junit.Test;

public class GameTest {

	private static final Universe UNIVERSE = new Universe(Arrays.asList(new Planet(1, 3), new Planet(6, 2)));

	@Test(expected = NotEnoughPlayersException.class)
	public void gameRequiresAtLeastOnePlayerToStart() throws Exception {
		new Game().start();
	}

	@Test(expected = IllegalStateException.class)
	public void gameRequiresAUniverseToStart() throws Exception {
		Game g = new Game();
		g.addPlayer(new HumanPlayer());
		g.start();
	}

	@Test
	public void singlePlayerGameStarts() throws Exception {
		Game g = new Game();
		g.addPlayer(new HumanPlayer());
		g.setUniverse(UNIVERSE);
		g.start();
	}
}
