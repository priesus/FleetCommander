package de.spries.fleetcommander.persistence

import org.apache.commons.lang3.RandomStringUtils
import java.util.HashSet
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

enum class JoinCodes private constructor() {
    INSTANCE;

    private val gameIdPerCode: MutableMap<String, Int> = ConcurrentHashMap()
    private val codesPerGameId: MutableMap<Int, MutableCollection<String>> = ConcurrentHashMap()

    @Synchronized
    @Throws(JoinCodeLimitReachedException::class)
    fun create(gameId: Int): String {
        codesPerGameId.putIfAbsent(gameId, HashSet())
        val gameCodes = codesPerGameId[gameId]!!.toMutableList()
        if (gameCodes.size >= MAX_ACTIVE_CODES) {
            throw JoinCodeLimitReachedException("There are already " + MAX_ACTIVE_CODES
                    + " active codes for this game")
        }

        var code: String? = null
        do {
            code = RandomStringUtils.randomAlphanumeric(6).toLowerCase(Locale.ROOT)
        } while (gameCodes.contains(code) || code!!.contains("0") || code.contains("o"))

        gameIdPerCode[code] = gameId
        gameCodes.add(code)
        return code
    }

    @Synchronized
    @Throws(InvalidCodeException::class)
    fun invalidate(joinCode: String?): Int {
        if (joinCode == null) {
            throw InvalidCodeException("null is an invalid code")
        }
        val joinCodeLC = joinCode.toLowerCase(Locale.ROOT)
        val gameId = gameIdPerCode.remove(joinCodeLC) ?: throw InvalidCodeException("$joinCodeLC is an invalid code")
        codesPerGameId[gameId]!!.remove(joinCodeLC)
        return gameId
    }

    operator fun get(gameId: Int): Collection<String> {
        return codesPerGameId.getOrDefault(gameId, HashSet())
    }

    fun invalidateAll(gameId: Int) {
        val codes = get(gameId)
        for (code in codes) {
            gameIdPerCode.remove(code)
        }
        codesPerGameId.remove(gameId)
    }

    fun reset() {
        gameIdPerCode.clear()
        codesPerGameId.clear()
    }

    companion object {

        private const val MAX_ACTIVE_CODES = 5
    }
}
