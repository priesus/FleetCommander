package de.spries.fleetcommander.model.universe;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import de.spries.fleetcommander.model.player.Player;
import de.spries.fleetcommander.model.universe.Universe;
import de.spries.fleetcommander.model.universe.UniverseGenerator;

public class UniverseGeneratorTest {

	private static final Player JOHN = mock(Player.class);
	private static final Player OTHER_PLAYER = mock(Player.class);
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
		assertThat(universe.getHomePlanetOf(JOHN), is(notNullValue()));
	}

	@Test(expected = IllegalStateException.class)
	public void everyPlayerHasToHaveAHomePlanet() throws Exception {
		Universe universe = UniverseGenerator.generate(PLANET_COUNT, JOHN_ONLY);
		universe.getHomePlanetOf(OTHER_PLAYER);
	}
}
