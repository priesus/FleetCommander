package de.spries.fleetcommander.web

import de.spries.fleetcommander.web.dto.GamePlayer
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

enum class GameAuthenticator {
    INSTANCE;

    private val gamePlayerTokens: MutableMap<GamePlayer, String> = ConcurrentHashMap()

    @Synchronized
    fun createAuthToken(gamePlayer: GamePlayer): String {
        if (gamePlayerTokens.containsKey(gamePlayer)) {
            throw IllegalArgumentException("There already is a token for this player!")
        }
        val token = UUID.randomUUID().toString()
        gamePlayerTokens[gamePlayer] = token
        return token
    }

    fun deleteAuthToken(gamePlayer: GamePlayer) {
        if (!gamePlayerTokens.containsKey(gamePlayer)) {
            throw IllegalArgumentException("There is no token for this game and player!")
        }
        gamePlayerTokens.remove(gamePlayer)
    }

    fun isAuthTokenValid(gamePlayer: GamePlayer, token: String?): Boolean {
        return token == gamePlayerTokens[gamePlayer]
    }

    fun reset() {
        gamePlayerTokens.clear()
    }
}
