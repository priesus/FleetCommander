package de.spries.fleetcommander.service.core;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import de.spries.fleetcommander.service.core.dto.GamePlayer;

public class GameAuthenticatorTest {

	private static final String INVALID_TOKEN = "invalid token";
	private static final GamePlayer FIRST_GAME_FIRST_PLAYER = GamePlayer.forIds(1, 1);
	private static final GamePlayer FIRST_GAME_SECOND_PLAYER = GamePlayer.forIds(1, 2);
	private static final GamePlayer SECOND_GAME_FIRST_PLAYER = GamePlayer.forIds(2, 1);
	private static final GamePlayer INEXISTENT_GAME_PLAYER = GamePlayer.forIds(123, 123);

	private String firstGamePlayerToken;

	@Before
	public void setUp() {
		GameAuthenticator.INSTANCE.reset();
		firstGamePlayerToken = GameAuthenticator.INSTANCE.createAuthToken(FIRST_GAME_FIRST_PLAYER);
	}

	@Test(expected = IllegalArgumentException.class)
	public void cannotCreateTokenForGameAgain() throws Exception {
		GameAuthenticator.INSTANCE.createAuthToken(FIRST_GAME_FIRST_PLAYER);
	}

	@Test
	public void generatedTokenIsValid() throws Exception {
		assertThat(GameAuthenticator.INSTANCE.isAuthTokenValid(FIRST_GAME_FIRST_PLAYER, firstGamePlayerToken), is(true));
	}

	@Test
	public void invalidTokenIsNotValidForGame() throws Exception {
		assertThat(GameAuthenticator.INSTANCE.isAuthTokenValid(FIRST_GAME_FIRST_PLAYER, INVALID_TOKEN), is(false));
	}

	@Test
	public void generatedTokenIsNotValidForOtherGame() throws Exception {
		String secondGameToken = GameAuthenticator.INSTANCE.createAuthToken(SECOND_GAME_FIRST_PLAYER);
		assertThat(GameAuthenticator.INSTANCE.isAuthTokenValid(FIRST_GAME_FIRST_PLAYER, secondGameToken), is(false));
	}

	@Test
	public void generatedTokenIsNotValidForOtherPlayer() throws Exception {
		String secondGameToken = GameAuthenticator.INSTANCE.createAuthToken(FIRST_GAME_SECOND_PLAYER);
		assertThat(GameAuthenticator.INSTANCE.isAuthTokenValid(FIRST_GAME_FIRST_PLAYER, secondGameToken), is(false));
	}

	@Test
	public void deletingTokenInvalidatesToken() throws Exception {
		GameAuthenticator.INSTANCE.deleteAuthToken(FIRST_GAME_FIRST_PLAYER);
		assertThat(GameAuthenticator.INSTANCE.isAuthTokenValid(FIRST_GAME_FIRST_PLAYER, firstGamePlayerToken),
				is(false));
	}

	@Test(expected = IllegalArgumentException.class)
	public void cannotDeleteTokenForInexistentGameId() throws Exception {
		GameAuthenticator.INSTANCE.deleteAuthToken(INEXISTENT_GAME_PLAYER);
	}

}
