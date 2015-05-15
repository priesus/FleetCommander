package de.spries.fleetcommander.persistence;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.RandomStringUtils;

public enum JoinCodes {
	INSTANCE;

	private static final Integer MAX_ACTIVE_CODES = 5;
	private Map<String, Integer> gameIdPerCode;
	private Map<Integer, Collection<String>> codesPerGameId;

	private JoinCodes() {
		reset();
	}

	public synchronized String create(int gameId) throws JoinCodeLimitReachedException {
		codesPerGameId.putIfAbsent(gameId, new HashSet<>());
		Collection<String> gameCodes = codesPerGameId.get(gameId);
		if (gameCodes.size() >= MAX_ACTIVE_CODES) {
			throw new JoinCodeLimitReachedException("There are already " + MAX_ACTIVE_CODES
					+ " active codes for this game");
		}

		String code = null;
		do {
			code = RandomStringUtils.randomAlphanumeric(6).toLowerCase(Locale.ROOT);
		} while (gameCodes.contains(code) || code.contains("0") || code.contains("o"));

		gameIdPerCode.put(code, gameId);
		gameCodes.add(code);
		return code;
	}

	public synchronized int invalidate(String joinCode) throws InvalidCodeException {
		if (joinCode == null) {
			throw new InvalidCodeException("null is an invalid code");
		}
		String joinCodeLC = joinCode.toLowerCase(Locale.ROOT);
		Integer gameId = gameIdPerCode.remove(joinCodeLC);
		if (gameId == null) {
			throw new InvalidCodeException(joinCodeLC + " is an invalid code");
		}
		codesPerGameId.get(gameId).remove(joinCodeLC);
		return gameId;
	}

	public Collection<String> get(int gameId) {
		return codesPerGameId.getOrDefault(gameId, new HashSet<>());
	}

	public void invalidateAll(int gameId) {
		Collection<String> codes = get(gameId);
		for (String code : codes) {
			gameIdPerCode.remove(code);
		}
		codesPerGameId.remove(gameId);
	}

	protected void reset() {
		gameIdPerCode = new ConcurrentHashMap<>();
		codesPerGameId = new ConcurrentHashMap<>();
	}
}
