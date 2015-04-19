package de.spries.fleetcommander.model.core;

import org.apache.commons.lang3.StringUtils;

import de.spries.fleetcommander.model.core.common.IllegalActionException;
import de.spries.fleetcommander.model.core.universe.FactorySite;

public class Player {

	public static class InsufficientCreditsException extends IllegalActionException {

		public InsufficientCreditsException(String msg) {
			super(msg);
		}
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

	public boolean canAffordFactory() {
		return credits >= FactorySite.FACTORY_COST;
	}

	public void reduceCredits(int debit) {
		if (debit > credits) {
			throw new InsufficientCreditsException("You don't have sufficient credits!");
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (name == null ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Player other = (Player) obj;
		if (!StringUtils.equals(name, other.name)) {
			return false;
		}
		return true;
	}

	/**
	 * For use in tests only
	 */
	protected void setCredits(int credits) {
		this.credits = credits;
	}
}
