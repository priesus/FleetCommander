package de.spries.fleetcommander;

public class Player {

	private static final int DEFAULT_STARTING_CREDITS = 500;

	private String name;
	private int credits;

	public Player(String name) {
		this.name = name;
		credits = DEFAULT_STARTING_CREDITS;
	}

	public String getName() {
		return name;
	}

	public int getCredits() {
		return credits;
	}
}
