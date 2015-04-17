package de.spries.fleetcommander.service.core;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

public enum GameAuthenticator {
	INSTANCE;

	private Map<Integer, String> gameTokens;

	private GameAuthenticator() {
		reset();
	}

	public synchronized String createAuthToken(int gameId) {
		if (gameTokens.containsKey(gameId)) {
			throw new IllegalArgumentException("There already is a token for this game!");
		}
		String token = UUID.randomUUID().toString();
		gameTokens.put(gameId, token);
		return token;
	}

	public void deleteAuthToken(int gameId, String token) {
		if (!isAuthTokenValid(gameId, token)) {
			throw new IllegalArgumentException("Invalid game id and token combination");
		}
		gameTokens.remove(gameId);
	}

	public boolean isAuthTokenValid(int gameId, String token) {
		return StringUtils.equals(token, gameTokens.get(gameId));
	}

	protected void reset() {
		gameTokens = new ConcurrentHashMap<>();
	}
}
