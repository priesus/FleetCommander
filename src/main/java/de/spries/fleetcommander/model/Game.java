package de.spries.fleetcommander.model;

import java.util.ArrayList;

import de.spries.fleetcommander.model.player.Player;
import de.spries.fleetcommander.model.universe.Universe;

public class Game {
	public static class NotEnoughPlayersException extends Exception {
		// Nothing to do
	}

	private int id;
	private ArrayList<Player> players;
	private Universe universe;

	public Game(int id) {
		this.id = id;
		players = new ArrayList<>();
	}

	public Player createHumanPlayer(String name) {
		Player player = new Player(name);
		players.add(player);
		return player;
	}

	public void start() throws NotEnoughPlayersException {
		if (players.isEmpty()) {
			throw new NotEnoughPlayersException();
		}
		if (universe == null) {
			throw new IllegalStateException("No universe!");
		}
	}

	public void endTurn() {
		universe.runFactoryProductionCycle();
		universe.runShipTravellingCycle();
	}

	public int getId() {
		return id;
	}

	public Universe getUniverse() {
		return universe;
	}

	public void setUniverse(Universe universe) {
		this.universe = universe;
	}

}
