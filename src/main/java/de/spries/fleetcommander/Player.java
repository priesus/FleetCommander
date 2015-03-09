package de.spries.fleetcommander;

public class Player {

	public static class InsufficientCreditsException extends Exception {
		// Nothing to implement
	}

	public static final int STARTING_BALANCE = 500;

	private String name;
	private int credits;

	public Player(String name) {
		this.name = name;
		credits = STARTING_BALANCE;
	}

	public String getName() {
		return name;
	}

	public int getCredits() {
		return credits;
	}

	public void reduceCredits(int debit) throws InsufficientCreditsException {
		if (debit > credits) {
			throw new InsufficientCreditsException();
		}
		credits -= debit;
	}
}
