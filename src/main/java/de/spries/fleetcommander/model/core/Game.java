package de.spries.fleetcommander.model.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import de.spries.fleetcommander.model.core.common.IllegalActionException;
import de.spries.fleetcommander.model.core.universe.Universe;

public class Game {
	public enum GameStatus {
		PENDING,
		RUNNING,
		OVER
	}

	public static final int MAX_PLAYERS = 6;
	private int id;
	private List<Player> players;
	private Set<Player> turnFinishedPlayers;
	private Universe universe;
	private GameStatus status;
	private TurnEvents previousTurnEvents;
	private int nextPlayerId;

	public Game() {
		players = new ArrayList<>(MAX_PLAYERS);
		turnFinishedPlayers = new HashSet<>(MAX_PLAYERS);
		status = GameStatus.PENDING;
		nextPlayerId = 1;
	}

	public void addPlayer(Player player) {
		if (!GameStatus.PENDING.equals(status)) {
			throw new IllegalStateException("It's too late to add players");
		}
		if (players.size() >= MAX_PLAYERS) {
			throw new IllegalActionException("Limit of " + MAX_PLAYERS + " players reached");
		}
		players.add(player);

		assignPlayerId(player);
	}

	private synchronized void assignPlayerId(Player player) {
		player.setId(nextPlayerId++);
	}

	public void start() {
		if (players.size() < 2) {
			throw new IllegalStateException("At least 2 players required!");
		}
		if (universe == null) {
			throw new IllegalStateException("No universe!");
		}
		previousTurnEvents = new TurnEvents(players);
		universe.setEventBus(previousTurnEvents);
		status = GameStatus.RUNNING;

		notifyActivePlayersForNewTurn();
	}

	public GameStatus getStatus() {
		return status;
	}

	public void endTurn(Player player) {
		if (!players.contains(player)) {
			throw new IllegalArgumentException(player + " doesn't participate in this game");
		}
		if (!player.isActive()) {
			throw new IllegalArgumentException(player + " has been defeated");
		}

		if (turnFinishedPlayers.contains(player)) {
			throw new IllegalArgumentException(player + " already has finished the turn");
		}

		turnFinishedPlayers.add(player);
		if (turnFinishedPlayers.size() == players.stream().filter(Player::isActive).count()) {
			endTurn();
		}
	}

	protected void endTurn() {
		if (GameStatus.PENDING.equals(status)) {
			throw new IllegalStateException("Game is not in progress");
		}

		previousTurnEvents.clear();
		turnFinishedPlayers.clear();
		universe.runFactoryProductionCycle();
		universe.runShipTravellingCycle();

		handleNewDefeatedPlayers(players.stream().filter(p -> p.isActive())
				.filter(p -> null == universe.getHomePlanetOf(p)));

		long numActivePlayers = players.stream().filter(p -> p.isActive()).count();
		long numActiveHumanPlayers = players.stream().filter(p -> p.isActive() && p.isHumanPlayer()).count();
		if (numActivePlayers <= 1 || numActiveHumanPlayers < 1) {
			status = GameStatus.OVER;
		}

		if (!GameStatus.OVER.equals(status)) {
			notifyActivePlayersForNewTurn();
		}
	}

	private void handleNewDefeatedPlayers(Stream<Player> newDefeatedPlayers) {
		newDefeatedPlayers.forEach(p -> handleNewDefeatedPlayer(p));
	}

	private void handleNewDefeatedPlayer(Player newDefeatedPlayer) {
		newDefeatedPlayer.setActive(false);
		universe.handleDefeatedPlayer(newDefeatedPlayer);
	}

	private void notifyActivePlayersForNewTurn() {
		players.stream().filter(Player::isActive).forEach(p -> p.notifyNewTurn(this));
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

	public Player getPlayerWithId(int playerId) {
		return players.stream().filter(p -> p.getId() == playerId).findFirst().orElse(null);
	}

	public TurnEvents getPreviousTurnEvents() {
		return previousTurnEvents;
	}

}
