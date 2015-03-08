package de.spries.fleetcommander;

import java.util.Arrays;

import org.junit.Test;

public class GameIntegrationTest {

	@Test
	public void playthrough() throws Exception {
		Game g = new Game();
		Player player1 = g.createHumanPlayer("John");

		Universe u = UniverseGenerator.generate(10, Arrays.asList(player1));
		g.setUniverse(u);

		g.start();

		System.out.println(g);

		Planet homePlanet = u.getHomePlanet(player1);
		homePlanet.buildFactory(player1);
	}
}
