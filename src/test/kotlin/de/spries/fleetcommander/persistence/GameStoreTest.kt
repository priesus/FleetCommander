package de.spries.fleetcommander.persistence

import org.hamcrest.Matchers.hasItem
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.junit.Assert.assertThat
import org.mockito.Mockito.mock

import java.util.ArrayList
import java.util.concurrent.ForkJoinPool
import java.util.stream.Collectors

import org.junit.Before
import org.junit.Test

import de.spries.fleetcommander.model.core.Game

class GameStoreTest {

    @Before
    fun setUp() {
        GameStore.INSTANCE.reset()
    }

    @Test
    fun creatingGameReturnsNewGameId() {
        val gameId1 = GameStore.INSTANCE.create(mock(Game::class.java))
        assertThat(gameId1, `is`(1))
        val gameId2 = GameStore.INSTANCE.create(mock(Game::class.java))
        assertThat(gameId2, `is`(2))
    }

    @Test
    @Throws(Exception::class)
    fun creatingGameAddsGameToList() {
        val game = mock(Game::class.java)

        assertThat(GameStore.INSTANCE.games, not(hasItem(game)))
        GameStore.INSTANCE.create(game)
        assertThat(GameStore.INSTANCE.games, hasItem(game))
    }

    @Test
    fun gameReturnedByGetEqualsCreatedGame() {
        val game = mock(Game::class.java)
        val gameId = GameStore.INSTANCE.create(game)

        val storedGame = GameStore.INSTANCE[gameId]
        assertThat(storedGame, `is`(game))
    }

    @Test
    @Throws(Exception::class)
    fun deletingGameRemovesGameFromList() {
        val game = mock(Game::class.java)
        val gameId = GameStore.INSTANCE.create(game)

        GameStore.INSTANCE.delete(gameId)
        assertThat(GameStore.INSTANCE.games, not(hasItem(game)))
    }

    @Test
    @Throws(Exception::class)
    fun creatingGamesInParallelStillCreatesSequentialIDs() {
        val games = ArrayList<Game>()
        for (i in 0..99) {
            games.add(mock(Game::class.java))
        }

        val forkJoinPool = ForkJoinPool(100)
        val ids = forkJoinPool.submit<Set<Int>> { games.parallelStream().map { g -> GameStore.INSTANCE.create(g) }.collect(Collectors.toSet()) }
                .get()

        assertThat(ids, hasSize(100))
    }
}
