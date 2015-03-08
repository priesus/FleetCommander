package de.spries.fleetcommander;

import org.junit.Test;

public class GameIntegrationTest {

	@Test
	public void playthrough() throws Exception {
		Universe u = UniverseGenerator.generate(10);
		System.out.println(u);

		Game g = new Game();
		g.createHumanPlayer("John");
		g.setUniverse(u);
		g.start();

	}
}
