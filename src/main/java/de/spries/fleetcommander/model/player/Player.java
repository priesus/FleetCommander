package de.spries.fleetcommander.model.player;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Player {

	public static class InsufficientCreditsException extends Exception {
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

	public void reduceCredits(int debit) throws InsufficientCreditsException {
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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
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
