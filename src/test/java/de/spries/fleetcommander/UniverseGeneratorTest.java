package de.spries.fleetcommander;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class UniverseGeneratorTest {

	private static final int PLANET_COUNT = 80;

	@Test
	public void eachPlayerGetsADifferentHomePlanet() throws Exception {
		Universe u = UniverseGenerator.generate(PLANET_COUNT);
		assertThat(u.getPlanets(), hasSize(PLANET_COUNT));
	}
}
