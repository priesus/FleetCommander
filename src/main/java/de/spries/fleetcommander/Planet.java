package de.spries.fleetcommander;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import de.spries.fleetcommander.Player.InsufficientCreditsException;

public class Planet {

	public static class NotPlayersOwnPlanetException extends Exception {
		// Nothing to implement
	}

	public static class NoFactorySlotsAvailableException extends Exception {
		// Nothing to implement
	}

	public static final int FACTORY_COST = 100;
	public static final int CREDITS_PER_FACTORY_PER_TURN = 75;
	public static final int SHIPS_PER_FACTORY_PER_TURN = 1;
	public static final int HOME_PLANET_STARTING_SHIPS = 6;

	private final int coordinateX;
	private final int coordinateY;
	private final int factorySlots = 6;
	private Player inhabitant;
	private int shipCount;
	private int factoryCount = 0;

	/**
	 * Regular (yet uninhabited) planed
	 */
	public Planet(int x, int y) {
		coordinateX = x;
		coordinateY = y;
		shipCount = 0;
	}

	/**
	 * Home planet of a player
	 */
	public Planet(int x, int y, Player inhabitant) {
		coordinateX = x;
		coordinateY = y;
		shipCount = HOME_PLANET_STARTING_SHIPS;
		this.inhabitant = inhabitant;
	}

	public Player getInhabitant() {
		return inhabitant;
	}

	public boolean isHomePlanetOf(Player player) {
		if (player.equals(inhabitant)) {
			return true;
		}
		return false;
	}

	public boolean isInhabitedBy(Player player) {
		return player.equals(inhabitant);
	}

	public int getCoordinateX() {
		return coordinateX;
	}

	public int getCoordinateY() {
		return coordinateY;
	}

	public int getShipCount() {
		return shipCount;
	}

	public void runFactoryCycle() {
		shipCount += factoryCount * SHIPS_PER_FACTORY_PER_TURN;
		if (inhabitant != null) {
			inhabitant.addCredits(getProducedCreditsPerTurn());
		}
	}

	public double distanceTo(Planet other) {
		int distanceX = coordinateX - other.getCoordinateX();
		int distanceY = coordinateY - other.getCoordinateY();
		return Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));
	}

	public void buildFactory(Player player) throws NotPlayersOwnPlanetException, InsufficientCreditsException,
			NoFactorySlotsAvailableException {
		if (!player.equals(inhabitant)) {
			throw new NotPlayersOwnPlanetException();
		}
		if (factorySlots == factoryCount) {
			throw new NoFactorySlotsAvailableException();
		}
		player.reduceCredits(FACTORY_COST);
		factoryCount++;
	}

	public int getFactorySlotCount() {
		return factorySlots;
	}

	public int getFactoryCount() {
		return factoryCount;
	}

	public int getProducedCreditsPerTurn() {
		return factoryCount * CREDITS_PER_FACTORY_PER_TURN;
	}

	public int getProducedShipsPerTurn() {
		return factoryCount * SHIPS_PER_FACTORY_PER_TURN;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.NO_FIELD_NAMES_STYLE);
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