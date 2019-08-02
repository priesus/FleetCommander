package de.spries.fleetcommander.persistence

import com.nhaarman.mockitokotlin2.mock
import de.spries.fleetcommander.model.core.Game
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.hasItem
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.not
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import java.util.ArrayList
import java.util.concurrent.ForkJoinPool
import java.util.stream.Collectors

class GameStoreTest {

    private val gameRepo = GameRepository()

    @Before
    fun setUp() {
        gameRepo.reset()
    }

    @Test
    fun creatingGameReturnsNewGameId() {
        val gameId1 = gameRepo.create(mock())
        assertThat(gameId1, `is`(1))
        val gameId2 = gameRepo.create(mock())
        assertThat(gameId2, `is`(2))
    }

    @Test
    fun creatingGameAddsGameToList() {
        val game = mock<Game>()

        assertThat(gameRepo.getGames(), not(hasItem(game)))
        gameRepo.create(game)
        assertThat(gameRepo.getGames(), hasItem(game))
    }

    @Test
    fun gameReturnedByGetEqualsCreatedGame() {
        val game = mock<Game>()
        val gameId = gameRepo.create(game)

        val storedGame = gameRepo[gameId]
        assertThat(storedGame, `is`(game))
    }

    @Test
    fun deletingGameRemovesGameFromList() {
        val game = mock<Game>()
        val gameId = gameRepo.create(game)

        gameRepo.delete(gameId)
        assertThat(gameRepo.getGames(), not(hasItem(game)))
    }

    @Test
    fun creatingGamesInParallelStillCreatesSequentialIDs() {
        val games = ArrayList<Game>()
        for (i in 0..99) {
            games.add(mock())
        }

        val forkJoinPool = ForkJoinPool(100)
        val ids = forkJoinPool.submit<Set<Int>> { games.parallelStream().map { g -> gameRepo.create(g) }.collect(Collectors.toSet()) }
                .get()

        assertThat(ids, hasSize(100))
    }
}
