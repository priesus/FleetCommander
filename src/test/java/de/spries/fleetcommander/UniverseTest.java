package de.spries.fleetcommander;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.Test;

public class UniverseTest {

	private static final Planet PLANET1 = new Planet(0, 0);
	private static final Planet PLANET2 = new Planet(5, 5);

	@Test
	public void newUniverseHasPlanets() {
		Universe u = new Universe(Arrays.asList(PLANET1, PLANET2));
		assertThat(u.getPlanets(), hasSize(2));
	}

}
