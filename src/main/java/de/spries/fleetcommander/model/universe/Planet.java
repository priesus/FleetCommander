package de.spries.fleetcommander.model.universe;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import de.spries.fleetcommander.model.player.Player;
import de.spries.fleetcommander.model.player.Player.InsufficientCreditsException;
import de.spries.fleetcommander.model.universe.FactorySite.NoFactorySlotsAvailableException;

public class Planet {

	public static class NotPlayersOwnPlanetException extends Exception {
		// Nothing to implement
	}

	private static final int HOME_PLANET_STARTING_SHIPS = 6;

	private int id;
	private final int x;
	private final int y;
	private final boolean isHomePlanet;
	private Player inhabitant;
	private int shipCount;

	private FactorySite factorySite = new FactorySite();

	public Planet(int x, int y) {
		this(x, y, null);
	}

	public Planet(int x, int y, Player inhabitant) {
		this.x = x;
		this.y = y;
		if (inhabitant != null) {
			shipCount = HOME_PLANET_STARTING_SHIPS;
			this.inhabitant = inhabitant;
			isHomePlanet = true;
		} else {
			shipCount = 0;
			isHomePlanet = false;
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public boolean isHomePlanetOf(Player player) {
		if (isHomePlanet && player.equals(inhabitant)) {
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
		int distanceX = x - other.getX();
		int distanceY = y - other.getY();
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
		if (!isInhabited() && shipsToLand > 0) {
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