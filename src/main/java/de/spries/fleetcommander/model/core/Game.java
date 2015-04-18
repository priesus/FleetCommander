package de.spries.fleetcommander.model.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.spries.fleetcommander.model.core.common.IllegalActionException;
import de.spries.fleetcommander.model.core.universe.Universe;

public class Game {
	public static final int MAX_PLAYERS = 6;
	private int id;
	private List<Player> players;
	private Set<Player> turnFinishedPlayers;
	private Universe universe;
	private boolean hasStarted;

	public Game() {
		players = new ArrayList<>();
		turnFinishedPlayers = new HashSet<>();
		hasStarted = false;
	}

	public void addPlayer(Player player) {
		if (hasStarted) {
			throw new IllegalStateException("Game has already started");
		}
		if (players.size() >= MAX_PLAYERS) {
			throw new IllegalActionException("Limit of " + MAX_PLAYERS + " players reached");
		}
		players.add(player);
	}

	public void start() {
		if (players.size() < 2) {
			throw new IllegalStateException("At least 2 players required!");
		}
		if (universe == null) {
			throw new IllegalStateException("No universe!");
		}
		hasStarted = true;

		notifyAllPlayersForNewTurn();
	}

	public boolean isStarted() {
		return hasStarted;
	}

	public void endTurn(Player player) {
		if (!players.contains(player)) {
			throw new IllegalArgumentException(player + " doesn't participate in this game");
		}

		synchronized (turnFinishedPlayers) {
			if (turnFinishedPlayers.contains(player)) {
				throw new IllegalArgumentException(player + " already has finished the turn");
			}

			turnFinishedPlayers.add(player);
		}
		if (turnFinishedPlayers.size() == players.size()) {
			endTurn();
		}
	}

	protected void endTurn() {
		if (!hasStarted) {
			throw new IllegalStateException("Game has not started, yet");
		}
		universe.runFactoryProductionCycle();
		universe.runShipTravellingCycle();

		turnFinishedPlayers.clear();

		notifyAllPlayersForNewTurn();
	}

	private void notifyAllPlayersForNewTurn() {
		players.parallelStream().forEach((p) -> p.notifyNewTurn(this));
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Universe getUniverse() {
		return universe;
	}

	public void setUniverse(Universe universe) {
		this.universe = universe;
	}

	public List<Player> getPlayers() {
		return new ArrayList<>(players);
	}

}
