package de.spries.fleetcommander;

public class Player {

	private String name;
	private int credits;

	public Player(String name, int startingCredits) {
		this.name = name;
		credits = startingCredits;
	}

	public String getName() {
		return name;
	}

	public int getCredits() {
		return credits;
	}
}
