package de.spries.fleetcommander;

import java.util.Arrays;

import org.junit.Test;

public class GameIntegrationTest {

	@Test
	public void playthrough() throws Exception {
		Planet p1 = new Planet(3, 1);
		Planet p2 = new Planet(2, 7);
		Universe u = new Universe(Arrays.asList(p1, p2));
		System.out.println(u);

		Game g = new Game();
		g.addPlayer(new HumanPlayer());
		g.setUniverse(u);
		g.start();
	}
}
