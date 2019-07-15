package de.spries.fleetcommander.service.core

import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertThat

import org.junit.Before
import org.junit.Test

import de.spries.fleetcommander.service.core.dto.GamePlayer

class GameAuthenticatorTest {

    private lateinit var firstGamePlayerToken: String

    @Before
    fun setUp() {
        GameAuthenticator.INSTANCE.reset()
        firstGamePlayerToken = GameAuthenticator.INSTANCE.createAuthToken(FIRST_GAME_FIRST_PLAYER)
    }

    @Test(expected = IllegalArgumentException::class)
    @Throws(Exception::class)
    fun cannotCreateTokenForGameAgain() {
        GameAuthenticator.INSTANCE.createAuthToken(FIRST_GAME_FIRST_PLAYER)
    }

    @Test
    @Throws(Exception::class)
    fun generatedTokenIsValid() {
        assertThat(GameAuthenticator.INSTANCE.isAuthTokenValid(FIRST_GAME_FIRST_PLAYER, firstGamePlayerToken), `is`(true))
    }

    @Test
    @Throws(Exception::class)
    fun invalidTokenIsNotValidForGame() {
        assertThat(GameAuthenticator.INSTANCE.isAuthTokenValid(FIRST_GAME_FIRST_PLAYER, INVALID_TOKEN), `is`(false))
    }

    @Test
    @Throws(Exception::class)
    fun generatedTokenIsNotValidForOtherGame() {
        val secondGameToken = GameAuthenticator.INSTANCE.createAuthToken(SECOND_GAME_FIRST_PLAYER)
        assertThat(GameAuthenticator.INSTANCE.isAuthTokenValid(FIRST_GAME_FIRST_PLAYER, secondGameToken), `is`(false))
    }

    @Test
    @Throws(Exception::class)
    fun generatedTokenIsNotValidForOtherPlayer() {
        val secondGameToken = GameAuthenticator.INSTANCE.createAuthToken(FIRST_GAME_SECOND_PLAYER)
        assertThat(GameAuthenticator.INSTANCE.isAuthTokenValid(FIRST_GAME_FIRST_PLAYER, secondGameToken), `is`(false))
    }

    @Test
    @Throws(Exception::class)
    fun deletingTokenInvalidatesToken() {
        GameAuthenticator.INSTANCE.deleteAuthToken(FIRST_GAME_FIRST_PLAYER)
        assertThat(GameAuthenticator.INSTANCE.isAuthTokenValid(FIRST_GAME_FIRST_PLAYER, firstGamePlayerToken),
                `is`(false))
    }

    @Test(expected = IllegalArgumentException::class)
    @Throws(Exception::class)
    fun cannotDeleteTokenForInexistentGameId() {
        GameAuthenticator.INSTANCE.deleteAuthToken(INEXISTENT_GAME_PLAYER)
    }

    companion object {

        private const val INVALID_TOKEN = "invalid token"
        private val FIRST_GAME_FIRST_PLAYER = GamePlayer.forIds(1, 1)
        private val FIRST_GAME_SECOND_PLAYER = GamePlayer.forIds(1, 2)
        private val SECOND_GAME_FIRST_PLAYER = GamePlayer.forIds(2, 1)
        private val INEXISTENT_GAME_PLAYER = GamePlayer.forIds(123, 123)
    }

}
