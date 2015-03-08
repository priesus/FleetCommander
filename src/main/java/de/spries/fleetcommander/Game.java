package de.spries.fleetcommander;

import java.util.ArrayList;
import java.util.Arrays;

public class Game {
	public static class NotEnoughPlayersException extends Exception {
		// Nothing to do
	}

	private static final int DEFAULT_STARTING_CREDITS = 500;

	private ArrayList<Player> players;
	private Universe universe;

	public Game() {
		players = new ArrayList<>();
	}

	public Player createHumanPlayer(String name) {
		Player player = new Player(name, DEFAULT_STARTING_CREDITS);
		players.add(player);
		return player;
	}

	public void setUniverse(Universe universe) {
		this.universe = universe;
	}

	public void start() throws NotEnoughPlayersException {
		if (players.isEmpty()) {
			throw new NotEnoughPlayersException();
		}
		if (universe == null) {
			throw new IllegalStateException("No universe!");
		}
	}

	/**
	 * Temporary implementation for visualization
	 */
	@Override
	public String toString() {
		int maxX = 0;
		int maxY = 0;
		for (Planet p : universe.getPlanets()) {
			if (p.getCoordinateX() > maxX) {
				maxX = p.getCoordinateX();
			}
			if (p.getCoordinateY() > maxY) {
				maxY = p.getCoordinateY();
			}
		}
		char planetsMap[][] = new char[maxX + 1][maxY + 1];
		for (Planet p : universe.getPlanets()) {
			planetsMap[p.getCoordinateX()][p.getCoordinateY()] = 'X';
		}

		StringBuilder sb = new StringBuilder(maxX * (maxY + 1));

		for (char[] line : planetsMap) {
			sb.append(Arrays.toString(line));
			sb.append('\n');
		}

		return sb.toString();
	}
}
