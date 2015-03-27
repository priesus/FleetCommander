package de.spries.fleetcommander.rest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.security.GeneralSecurityException;

import org.junit.Before;
import org.junit.Test;

public class GameAuthenticatorTest {

	private static final String INVALID_TOKEN = "invalid token";
	private static final int FIRST_GAME_ID = 1;
	private static final int SECOND_GAME_ID = 2;
	private static final int INEXISTENT_GAME_ID = 12345678;

	private String firstGameToken;

	@Before
	public void setUp() {
		GameAuthenticator.INSTANCE.reset();
		firstGameToken = GameAuthenticator.INSTANCE.createAuthToken(FIRST_GAME_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void cannotCreateTokenForGameAgain() throws Exception {
		GameAuthenticator.INSTANCE.createAuthToken(FIRST_GAME_ID);
	}

	@Test
	public void generatedTokenIsValid() throws Exception {
		assertThat(GameAuthenticator.INSTANCE.isAuthTokenValid(FIRST_GAME_ID, firstGameToken), is(true));
	}

	@Test
	public void invalidTokenIsNotValidForGame() throws Exception {
		assertThat(GameAuthenticator.INSTANCE.isAuthTokenValid(FIRST_GAME_ID, INVALID_TOKEN), is(false));
	}

	@Test
	public void generatedTokenIsNotValidForOtherGame() throws Exception {
		String secondGameToken = GameAuthenticator.INSTANCE.createAuthToken(SECOND_GAME_ID);
		assertThat(GameAuthenticator.INSTANCE.isAuthTokenValid(FIRST_GAME_ID, secondGameToken), is(false));
	}

	@Test
	public void tokenIsNotValidForInexistentGame() throws Exception {
		assertThat(GameAuthenticator.INSTANCE.isAuthTokenValid(SECOND_GAME_ID, firstGameToken), is(false));
	}

	@Test
	public void deletingTokenInvalidatesToken() throws Exception {
		GameAuthenticator.INSTANCE.deleteAuthToken(FIRST_GAME_ID, firstGameToken);
		assertThat(GameAuthenticator.INSTANCE.isAuthTokenValid(FIRST_GAME_ID, firstGameToken), is(false));
	}

	@Test(expected = GeneralSecurityException.class)
	public void cannotDeleteTokenForInvalidToken() throws Exception {
		GameAuthenticator.INSTANCE.deleteAuthToken(FIRST_GAME_ID, INVALID_TOKEN);
	}

	@Test(expected = GeneralSecurityException.class)
	public void cannotDeleteTokenForInexistentGameId() throws Exception {
		GameAuthenticator.INSTANCE.deleteAuthToken(INEXISTENT_GAME_ID, firstGameToken);
	}

}
