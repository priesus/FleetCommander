package de.spries.fleetcommander;

import java.util.ArrayList;

public class Game {

	private ArrayList<HumanPlayer> players;
	private Universe universe;

	public Game() {
		players = new ArrayList<>();
	}

	public void addPlayer(HumanPlayer humanPlayer) {
		players.add(humanPlayer);
	}

	public void start() throws NotEnoughPlayersException {
		if (players.isEmpty()) {
			throw new NotEnoughPlayersException();
		}
		if (universe == null) {
			throw new IllegalStateException("No universe!");
		}
	}

	public void setUniverse(Universe universe) {
		this.universe = universe;
	}
}
