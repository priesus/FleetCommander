package de.spries.fleetcommander.model.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

import de.spries.fleetcommander.model.core.common.IllegalActionException;
import de.spries.fleetcommander.model.core.universe.Universe;
import de.spries.fleetcommander.model.core.universe.UniverseFactory;

public class Game {
	public enum Status {
		PENDING,
		RUNNING,
		OVER
	}

	public static final int MAX_PLAYERS = 6;
	private int id;
	private Collection<Player> players;
	private Universe universe;
	private Status status;
	private TurnEvents previousTurnEvents;
	private int nextPlayerId;
	private int turnNumber;

	public Game() {
		players = new HashSet<>(MAX_PLAYERS);
		status = Status.PENDING;
		nextPlayerId = 1;
		turnNumber = 0;
	}

	public void addPlayer(Player player) {
		if (!Status.PENDING.equals(status)) {
			throw new IllegalActionException("It's too late to add players");
		}
		if (players.size() >= MAX_PLAYERS) {
			throw new IllegalActionException("Limit of " + MAX_PLAYERS + " players reached");
		}
		if (players.contains(player)) {
			throw new IllegalActionException("There is already a player named " + player.getName());
		}

		assignPlayerId(player);
		players.add(player);
	}

	private synchronized void assignPlayerId(Player player) {
		player.setId(nextPlayerId++);
	}

	public void start(Player player) {
		if (!players.contains(player)) {
			throw new IllegalActionException("You are not participating in this game");
		}
		if (Status.PENDING != status) {
			throw new IllegalActionException("The game has started already");
		}
		if (players.size() < 2) {
			throw new IllegalActionException("At least 2 players required to start the game!");
		}

		player.setReady();
		tryStart();
	}

	private void tryStart() {
		if (countReadyPlayers() == players.size()) {
			start();
		}
	}

	protected void start() {
		turnNumber++;
		previousTurnEvents = new TurnEvents(players);
		universe = UniverseFactory.generate(players);
		universe.setEventBus(previousTurnEvents);
		status = Status.RUNNING;

		resetReadyStatusOnPlayers();
		notifyActivePlayersForNewTurn();
	}

	public Status getStatus() {
		return status;
	}

	//TODO players should not be able to make any actions between ending their turn and the actual end of the turn
	public void endTurn(Player player) {
		if (!players.contains(player)) {
			throw new IllegalActionException(player
					+ " doesn't participate in this game and therefore cannot end the turn");
		}
		if (!player.isActive()) {
			throw new IllegalActionException(player + " has been defeated and therefore cannot end the turn");
		}

		player.setReady();
		tryEndTurn();
	}

	private void tryEndTurn() {
		if (countReadyPlayers() == countActivePlayers()) {
			endTurn();
		}
	}

	protected void endTurn() {
		if (Status.PENDING.equals(status)) {
			throw new IllegalActionException("Game is not in progress, yet");
		}

		turnNumber++;
		previousTurnEvents.clear();
		resetReadyStatusOnPlayers();
		universe.resetPreviousTurnMarkers();
		universe.runFactoryProductionCycle();
		universe.runShipTravellingCycle();

		handleNewDefeatedPlayers(players.stream().filter(p -> p.isActive())
				.filter(p -> null == universe.getHomePlanetOf(p)));

		long numActivePlayers = players.stream().filter(p -> p.isActive()).count();
		long numActiveHumanPlayers = countActiveHumanPlayers();
		if (numActivePlayers <= 1 || numActiveHumanPlayers < 1) {
			status = Status.OVER;
		}

		if (!Status.OVER.equals(status)) {
			notifyActivePlayersForNewTurn();
		}
	}

	public void quit(Player player) {
		if (!players.contains(player)) {
			throw new IllegalActionException(player + " doesn't participate in this game");
		}

		if (Status.PENDING.equals(status)) {
			players.remove(player);
		} else if (Status.RUNNING.equals(status)) {
			player.handleQuit();
			handleNewDefeatedPlayer(player);
			tryEndTurn();
		}

		if (countActiveHumanPlayers() < 1) {
			status = Status.OVER;
		}
	}

	private void handleNewDefeatedPlayers(Stream<Player> newDefeatedPlayers) {
		newDefeatedPlayers.forEach(p -> handleNewDefeatedPlayer(p));
	}

	private void handleNewDefeatedPlayer(Player newDefeatedPlayer) {
		newDefeatedPlayer.handleDefeat();
		universe.handleDefeatedPlayer(newDefeatedPlayer);
	}

	private void notifyActivePlayersForNewTurn() {
		players.stream().filter(Player::isActive).forEach(p -> p.notifyNewTurn(this));
	}

	private void resetReadyStatusOnPlayers() {
		players.stream().filter(p -> p.isReady()).forEach(p -> p.setPlaying());
	}

	private long countActiveHumanPlayers() {
		return players.stream().filter(p -> p.isActive() && p.isHumanPlayer()).count();
	}

	private long countActivePlayers() {
		return players.stream().filter(p -> p.isActive()).count();
	}

	private long countReadyPlayers() {
		return players.stream().filter(p -> p.isReady()).count();
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

	public List<Player> getPlayers() {
		return new ArrayList<>(players);
	}

	public Player getPlayerWithId(int playerId) {
		return players.stream().filter(p -> p.getId() == playerId).findFirst().orElse(null);
	}

	public TurnEvents getPreviousTurnEvents() {
		return previousTurnEvents;
	}

	public int getTurnNumber() {
		return turnNumber;
	}

}
