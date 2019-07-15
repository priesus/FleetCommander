package de.spries.fleetcommander.persistence

import de.spries.fleetcommander.model.core.Game
import java.util.concurrent.ConcurrentHashMap

enum class GameStore {
    INSTANCE;

    private val gameStore: MutableMap<Int, Game> = ConcurrentHashMap()
    private var nextGameId: Int = 1

    val games: Collection<Game>
        get() = gameStore.values

    @Synchronized
    fun create(game: Game): Int {
        gameStore[nextGameId] = game
        return nextGameId++
    }

    operator fun get(id: Int): Game? {
        return gameStore[id]
    }

    fun delete(id: Int) {
        gameStore.remove(id)
    }

    fun reset() {
        gameStore.clear()
        nextGameId = 1
    }
}
