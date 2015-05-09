package de.spries.fleetcommander.service.core;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import de.spries.fleetcommander.service.core.dto.GamePlayer;

public enum GameAuthenticator {
	INSTANCE;

	private Map<GamePlayer, String> gamePlayerTokens;

	private GameAuthenticator() {
		reset();
	}

	public synchronized String createAuthToken(GamePlayer gamePlayer) {
		if (gamePlayerTokens.containsKey(gamePlayer)) {
			throw new IllegalArgumentException("There already is a token for this player!");
		}
		String token = UUID.randomUUID().toString();
		gamePlayerTokens.put(gamePlayer, token);
		return token;
	}

	public void deleteAuthToken(GamePlayer gamePlayer) {
		if (!gamePlayerTokens.containsKey(gamePlayer)) {
			throw new IllegalArgumentException("There is no token for this game and player!");
		}
		gamePlayerTokens.remove(gamePlayer);
	}

	public boolean isAuthTokenValid(GamePlayer gamePlayer, String token) {
		return StringUtils.equals(token, gamePlayerTokens.get(gamePlayer));
	}

	protected void reset() {
		gamePlayerTokens = new ConcurrentHashMap<>();
	}
}
