package de.spries.fleetcommander.model.core;

import java.util.List;
import java.util.stream.Collectors;

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

	private int id;
	private String name;
	private int credits;
	private boolean active;

	public Player(String name) {
		id = -1;
		this.name = name;
		credits = STARTING_CREDITS;
		active = true;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public int getCredits() {
		return credits;
	}

	public boolean isActive() {
		return active;
	}

	public boolean isHumanPlayer() {
		return true;
	}

	public void setActive(boolean active) {
		this.active = active;
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
		result = prime * result + id;
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
		if (id != other.id) {
			return false;
		}
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

	public static List<Player> filterAllOtherPlayers(List<Player> players, Player viewingPlayer) {
		return players.stream()
				.filter(p -> !p.equals(viewingPlayer)).collect(Collectors.toList());
	}
}
