package de.spries.fleetcommander.model.core.universe;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import de.spries.fleetcommander.model.core.Player;

public class UniverseFactoryTest {

	private static final Player JOHN = mock(Player.class);
	private static final Player OTHER_PLAYER = mock(Player.class);
	private static final List<Player> JOHN_ONLY = Arrays.asList(JOHN);

	@Test
	public void generatedUniverseHasMorePlanetsThanPlayers() throws Exception {
		Universe universe = UniverseFactory.generate(JOHN_ONLY);
		assertThat(universe.getPlanets().size(), greaterThan(JOHN_ONLY.size()));
	}

	@Test
	public void playerHasHomePlanet() throws Exception {
		Universe universe = UniverseFactory.generate(JOHN_ONLY);
		assertThat(universe.getHomePlanetOf(JOHN), is(notNullValue()));
	}

	@Test
	public void playersNotParticipatingInGameDontHaveAHomePlanet() throws Exception {
		Universe universe = UniverseFactory.generate(JOHN_ONLY);
		assertThat(universe.getHomePlanetOf(OTHER_PLAYER), is(nullValue()));
	}

	@Test
	public void everyPlanetHasAUniqueId() throws Exception {
		Universe universe = UniverseFactory.generate(JOHN_ONLY);
		Set<Integer> planetIds = universe.getPlanets().stream().map(p -> p.getId())
				.collect(Collectors.toSet());

		assertThat(planetIds.size(), is(universe.getPlanets().size()));
	}
}
