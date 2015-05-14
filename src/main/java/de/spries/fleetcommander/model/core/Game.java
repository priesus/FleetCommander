package de.spries.fleetcommander.model.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.spries.fleetcommander.model.core.common.IllegalActionException;
import de.spries.fleetcommander.model.core.universe.Universe;
import de.spries.fleetcommander.model.core.universe.UniverseFactory;

public class Game {
	public enum GameStatus {
		PENDING,
		RUNNING,
		OVER
	}

	public static final int MAX_PLAYERS = 6;
	private int id;
	private List<Player> players;
	private Set<Player> readyPlayers;
	private Universe universe;
	private GameStatus status;
	private TurnEvents previousTurnEvents;
	private int nextPlayerId;

	public Game() {
		players = new ArrayList<>(MAX_PLAYERS);
		readyPlayers = new HashSet<>(MAX_PLAYERS);
		status = GameStatus.PENDING;
		nextPlayerId = 1;
	}

	public void addPlayer(Player player) {
		if (!GameStatus.PENDING.equals(status)) {
			throw new IllegalActionException("It's too late to add players");
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

	public void start(Player player) {
		if (!players.contains(player)) {
			throw new IllegalActionException("You are not participating in this game");
		}
		if (GameStatus.PENDING != status) {
			throw new IllegalActionException("The game has started already");
		}
		if (players.size() < 2) {
			throw new IllegalActionException("At least 2 players required in order to start the game!");
		}
		if (readyPlayers.contains(player)) {
			throw new IllegalActionException("You have to wait for the other players to start the game");
		}

		readyPlayers.add(player);

		tryStart();
	}

	private void tryStart() {
		if (players.size() == readyPlayers.size()) {
			start();
		}
	}

	private void start() {
		previousTurnEvents = new TurnEvents(players);
		universe = UniverseFactory.generate(players);
		universe.setEventBus(previousTurnEvents);
		status = GameStatus.RUNNING;

		readyPlayers.clear();
		notifyActivePlayersForNewTurn();
	}

	public GameStatus getStatus() {
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

		if (readyPlayers.contains(player)) {
			throw new IllegalActionException(player + " has already finished the turn");
		}

		readyPlayers.add(player);
		tryEndTurn();
	}

	private void tryEndTurn() {
		List<Player> activePlayers = players.stream().filter(Player::isActive).collect(Collectors.toList());
		if (readyPlayers.containsAll(activePlayers)) {
			endTurn();
		}
	}

	protected void endTurn() {
		if (GameStatus.PENDING.equals(status)) {
			throw new IllegalActionException("Game is not in progress, yet");
		}

		previousTurnEvents.clear();
		readyPlayers.clear();
		universe.runFactoryProductionCycle();
		universe.runShipTravellingCycle();

		handleNewDefeatedPlayers(players.stream().filter(p -> p.isActive())
				.filter(p -> null == universe.getHomePlanetOf(p)));

		long numActivePlayers = players.stream().filter(p -> p.isActive()).count();
		long numActiveHumanPlayers = countActiveHumanPlayers();
		if (numActivePlayers <= 1 || numActiveHumanPlayers < 1) {
			status = GameStatus.OVER;
		}

		if (!GameStatus.OVER.equals(status)) {
			notifyActivePlayersForNewTurn();
		}
	}

	public void quit(Player player) {
		if (!players.contains(player)) {
			throw new IllegalActionException(player + " doesn't participate in this game");
		}
		if (player.hasQuit()) {
			throw new IllegalActionException(player + " has already quit");
		}

		if (GameStatus.PENDING.equals(status)) {
			players.remove(player);
		} else if (GameStatus.RUNNING.equals(status)) {
			player.handleQuit();
			handleNewDefeatedPlayer(player);
			tryEndTurn();
		}

		if (countActiveHumanPlayers() < 1) {
			status = GameStatus.OVER;
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

	private long countActiveHumanPlayers() {
		return players.stream().filter(p -> p.isActive() && p.isHumanPlayer()).count();
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

}
