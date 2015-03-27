package de.spries.fleetcommander.persistence;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import de.spries.fleetcommander.model.Game;

public class GameStoreTest {

	@Before
	public void setUp() {
		GameStore.INSTANCE.reset();
	}

	@Test
	public void creatingGameReturnsNewGameId() {
		int gameId1 = GameStore.INSTANCE.create(mock(Game.class));
		assertThat(gameId1, is(1));
		int gameId2 = GameStore.INSTANCE.create(mock(Game.class));
		assertThat(gameId2, is(2));
	}

	@Test
	public void creatingGameAddsGameToList() throws Exception {
		Game game = mock(Game.class);

		assertThat(GameStore.INSTANCE.getGames(), not(hasItem(game)));
		GameStore.INSTANCE.create(game);
		assertThat(GameStore.INSTANCE.getGames(), hasItem(game));
	}

	@Test
	public void gameReturnedByGetEqualsCreatedGame() {
		Game game = mock(Game.class);
		int gameId = GameStore.INSTANCE.create(game);

		Game storedGame = GameStore.INSTANCE.get(gameId);
		assertThat(storedGame, is(game));
	}

	@Test
	public void deletingGameRemovesGameFromList() throws Exception {
		Game game = mock(Game.class);
		int gameId = GameStore.INSTANCE.create(game);

		GameStore.INSTANCE.delete(gameId);
		assertThat(GameStore.INSTANCE.getGames(), not(hasItem(game)));
	}

	@Test
	public void creatingGamesInParallelStillCreatesSequentialIDs() throws Exception {
		List<Game> games = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			games.add(mock(Game.class));
		}

		ForkJoinPool forkJoinPool = new ForkJoinPool(100);
		Set<Integer> ids = forkJoinPool.submit(
				() -> games.parallelStream().map((g) -> GameStore.INSTANCE.create(g)).collect(Collectors.toSet()))
				.get();

		assertThat(ids, hasSize(100));
	}
}
