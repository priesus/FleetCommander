package de.spries.fleetcommander;

import java.util.ArrayList;

public class Game {
	public static class NotEnoughPlayersException extends Exception {
		// Nothing to do
	}

	private ArrayList<Player> players;
	private Universe universe;

	public Game() {
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
		universe.runFactoryCycle();
		universe.runShipTravellingCycle();
	}

	public void setUniverse(Universe universe) {
		this.universe = universe;
	}

}
