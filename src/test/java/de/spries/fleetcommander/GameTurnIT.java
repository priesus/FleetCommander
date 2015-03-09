package de.spries.fleetcommander;

import java.util.Arrays;

import org.junit.Test;

public class GameTurnIT {

	@Test
	public void playthrough() throws Exception {
		Game game = new Game();
		Player player1 = game.createHumanPlayer("John");

		Universe u = UniverseGenerator.generate(10, Arrays.asList(player1));
		game.setUniverse(u);

		game.start();

		System.out.println(game);

		Planet homePlanet = u.getHomePlanet(player1);
		homePlanet.buildFactory(player1);
	}
}
