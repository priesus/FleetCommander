package de.spries.fleetcommander.web

import de.spries.fleetcommander.web.dto.GamePlayer
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test

class GameAuthenticatorTest {

    private lateinit var firstGamePlayerToken: String
    private val gameAuth = GameAuthenticator()

    @Before
    fun setUp() {
        gameAuth.reset()
        firstGamePlayerToken = gameAuth.createAuthToken(FIRST_GAME_FIRST_PLAYER)
    }

    @Test(expected = IllegalArgumentException::class)
    fun cannotCreateTokenForGameAgain() {
        gameAuth.createAuthToken(FIRST_GAME_FIRST_PLAYER)
    }

    @Test
    fun generatedTokenIsValid() {
        assertThat(gameAuth.isAuthTokenValid(FIRST_GAME_FIRST_PLAYER, firstGamePlayerToken), `is`(true))
    }

    @Test
    fun invalidTokenIsNotValidForGame() {
        assertThat(gameAuth.isAuthTokenValid(FIRST_GAME_FIRST_PLAYER, INVALID_TOKEN), `is`(false))
    }

    @Test
    fun generatedTokenIsNotValidForOtherGame() {
        val secondGameToken = gameAuth.createAuthToken(SECOND_GAME_FIRST_PLAYER)
        assertThat(gameAuth.isAuthTokenValid(FIRST_GAME_FIRST_PLAYER, secondGameToken), `is`(false))
    }

    @Test
    fun generatedTokenIsNotValidForOtherPlayer() {
        val secondGameToken = gameAuth.createAuthToken(FIRST_GAME_SECOND_PLAYER)
        assertThat(gameAuth.isAuthTokenValid(FIRST_GAME_FIRST_PLAYER, secondGameToken), `is`(false))
    }

    @Test
    fun deletingTokenInvalidatesToken() {
        gameAuth.deleteAuthToken(FIRST_GAME_FIRST_PLAYER)
        assertThat(gameAuth.isAuthTokenValid(FIRST_GAME_FIRST_PLAYER, firstGamePlayerToken),
                `is`(false))
    }

    @Test(expected = IllegalArgumentException::class)
    fun cannotDeleteTokenForInexistentGameId() {
        gameAuth.deleteAuthToken(INEXISTENT_GAME_PLAYER)
    }

    companion object {

        private const val INVALID_TOKEN = "invalid token"
        private val FIRST_GAME_FIRST_PLAYER = GamePlayer(1, 1)
        private val FIRST_GAME_SECOND_PLAYER = GamePlayer(1, 2)
        private val SECOND_GAME_FIRST_PLAYER = GamePlayer(2, 1)
        private val INEXISTENT_GAME_PLAYER = GamePlayer(123, 123)
    }

}
