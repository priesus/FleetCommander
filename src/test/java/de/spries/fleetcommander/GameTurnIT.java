package de.spries.fleetcommander;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class GameTurnIT {

	private Game game;
	private Player john;
	private Universe universe;

	@Before
	public void setUp() throws Exception {
		game = new Game();
		john = game.createHumanPlayer("John");
		universe = UniverseGenerator.generate(10, Arrays.asList(john));
		game.setUniverse(universe);
		game.start();
	}

	@Test
	public void factoryIncreasesPlayerCreditsOnTurnEnd() throws Exception {
		Planet homePlanet = universe.getHomePlanetOf(john);
		homePlanet.buildFactory(john);

		int creditsBefore = john.getCredits();
		game.endTurn();

		assertThat(john.getCredits(), is(creditsBefore + Planet.CREDITS_PER_FACTORY_PER_TURN));
	}

	@Test
	public void twoFactoryIncreasePlayerCreditsOnTurnEnd() throws Exception {
		Planet homePlanet = universe.getHomePlanetOf(john);
		homePlanet.buildFactory(john);
		homePlanet.buildFactory(john);

		int creditsBefore = john.getCredits();
		game.endTurn();

		assertThat(john.getCredits(), is(creditsBefore + 2 * Planet.CREDITS_PER_FACTORY_PER_TURN));
	}

	@Test
	public void playerWithoutFactoriesDoesntEarnCreditsOnTurnEnd() throws Exception {
		int creditsBefore = john.getCredits();
		game.endTurn();
		assertThat(john.getCredits(), is(creditsBefore));
	}
}
