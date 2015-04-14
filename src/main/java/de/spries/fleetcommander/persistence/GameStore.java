package de.spries.fleetcommander.persistence;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.spries.fleetcommander.model.core.Game;

public enum GameStore {
	INSTANCE;

	private Map<Integer, Game> gameStore;
	private int nextGameId;

	private GameStore() {
		reset();
	}

	public synchronized int create(Game game) {
		gameStore.put(nextGameId, game);
		return nextGameId++;
	}

	public Game get(int id) {
		return gameStore.get(id);
	}

	public void delete(int id) {
		gameStore.remove(id);
	}

	protected Collection<Game> getGames() {
		return gameStore.values();
	}

	protected void reset() {
		gameStore = new ConcurrentHashMap<>();
		nextGameId = 1;
	}
}
