package de.spries.fleetcommander;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import de.spries.fleetcommander.FactorySite.NoFactorySlotsAvailableException;
import de.spries.fleetcommander.Player.InsufficientCreditsException;

public class Planet {

	public static class NotPlayersOwnPlanetException extends Exception {
		// Nothing to implement
	}

	private static final int HOME_PLANET_STARTING_SHIPS = 6;

	private final int coordinateX;
	private final int coordinateY;
	private Player inhabitant;
	private int shipCount;

	private FactorySite factorySite = new FactorySite();

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

	public boolean isInhabited() {
		return inhabitant != null;
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

	public void runProductionCycle() {
		shipCount += factorySite.getProducedShipsPerTurn();
		if (inhabitant != null) {
			inhabitant.addCredits(factorySite.getProducedCreditsPerTurn());
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
		if (factorySite.hasAvailableSlots()) {
			player.reduceCredits(FactorySite.FACTORY_COST);
		}
		factorySite.buildFactory();
	}

	public void sendShipsAway(int shipsToSend, Player player) throws NotPlayersOwnPlanetException,
	NotEnoughShipsException {
		if (!player.equals(inhabitant)) {
			throw new NotPlayersOwnPlanetException();
		}
		if (shipsToSend > shipCount) {
			throw new NotEnoughShipsException();
		}

		shipCount -= shipsToSend;
	}

	public void landShips(int shipsToLand, Player player) {
		if (!isInhabited()) {
			inhabitant = player;
		}
		shipCount += shipsToLand;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	/**
	 * for tests only!
	 */
	protected void setFactorySite(FactorySite factorySite) {
		this.factorySite = factorySite;
	}
}