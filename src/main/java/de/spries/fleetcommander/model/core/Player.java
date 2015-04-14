package de.spries.fleetcommander.model.core;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import de.spries.fleetcommander.model.core.common.IllegalActionException;

public class Player {

	public static class InsufficientCreditsException extends IllegalActionException {
		// Nothing to implement
	}

	protected static final int STARTING_CREDITS = 500;
	protected static final int MAX_CREDITS = 99_999;

	private String name;
	private int credits;

	public Player(String name) {
		this.name = name;
		credits = STARTING_CREDITS;
	}

	public String getName() {
		return name;
	}

	public int getCredits() {
		return credits;
	}

	public void reduceCredits(int debit) {
		if (debit > credits) {
			throw new InsufficientCreditsException();
		}
		credits -= debit;
	}

	public void addCredits(int creditsToAdd) {
		credits += creditsToAdd;
		if (credits > MAX_CREDITS) {
			credits = MAX_CREDITS;
		}
	}

	@SuppressWarnings("unused")
	public void notifyNewTurn(Game game) {
		//Nothing to do
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
}
