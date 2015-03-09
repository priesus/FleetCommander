package de.spries.fleetcommander;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class UniverseGeneratorTest {

	private static final Player JOHN = new Player("John");
	private static final List<Player> JOHN_ONLY = Arrays.asList(JOHN);
	private static final int PLANET_COUNT = 80;

	@Test
	public void generatedUniverseHasDesiredSize() throws Exception {
		Universe universe = UniverseGenerator.generate(PLANET_COUNT, JOHN_ONLY);
		assertThat(universe.getPlanets(), hasSize(PLANET_COUNT));
	}

	@Test
	public void playerHasHomePlanet() throws Exception {
		Universe universe = UniverseGenerator.generate(PLANET_COUNT, JOHN_ONLY);
		assertThat(universe.getHomePlanet(JOHN), is(notNullValue()));
	}

	@Test(expected = IllegalStateException.class)
	public void everyPlayerHasToHaveAHomePlanet() throws Exception {
		Universe universe = UniverseGenerator.generate(PLANET_COUNT, JOHN_ONLY);
		universe.getHomePlanet(new Player("Other player"));
	}
}
