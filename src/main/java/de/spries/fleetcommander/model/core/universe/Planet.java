package de.spries.fleetcommander.model.core.universe;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import de.spries.fleetcommander.model.core.Player;
import de.spries.fleetcommander.model.core.common.IllegalActionException;

public class Planet {

	private static final int HOME_PLANET_STARTING_SHIPS = 6;

	private int id;
	private final int x;
	private final int y;
	private boolean isHomePlanet;
	private Player inhabitant;
	private float shipCount;
	private Map<Player, Integer> incomingShipsPerPlayer;

	private FactorySite factorySite = new FactorySite();

	public Planet(int x, int y) {
		this(x, y, null);
	}

	public Planet(int x, int y, Player inhabitant) {
		this.x = x;
		this.y = y;
		incomingShipsPerPlayer = new HashMap<>();
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

	public boolean isHomePlanet() {
		return isHomePlanet;
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

	public Player getInhabitant() {
		return inhabitant;
	}

	public int getShipCount() {
		return (int) shipCount;
	}

	public FactorySite getFactorySite() {
		return factorySite;
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

	public void buildFactory(Player player) {
		if (!canBuildFactory(player)) {
			throw new IllegalActionException();
		}
		player.reduceCredits(FactorySite.FACTORY_COST);
		factorySite.buildFactory();
	}

	public boolean canBuildFactory(Player player) {
		if (!player.equals(inhabitant)) {
			return false;
		}
		if (!factorySite.hasAvailableSlots()) {
			return false;
		}
		if (player.getCredits() < FactorySite.FACTORY_COST) {
			return false;
		}
		return true;
	}

	public void sendShipsAway(int shipsToSend, Player player) {
		if (!player.equals(inhabitant)) {
			throw new IllegalActionException();
		}
		if (shipsToSend > shipCount) {
			throw new IllegalActionException();
		}

		shipCount -= shipsToSend;
	}

	public void addIncomingShips(int ships, Player player) {
		incomingShipsPerPlayer.putIfAbsent(player, 0);
		incomingShipsPerPlayer.put(player, incomingShipsPerPlayer.get(player) + ships);

	}

	public int getIncomingShipCount(Player player) {
		Integer incShips = incomingShipsPerPlayer.get(player);
		return incShips != null ? incShips : 0;
	}

	public void landShips(int shipsToLand, Player invader) {
		if (shipsToLand <= 0) {
			throw new IllegalArgumentException("Cannot land " + shipsToLand + " ships");
		}
		if (!isInhabited()) {
			inhabitant = invader;
			shipCount += shipsToLand;
		}
		else if (isInhabitedBy(invader)) {
			shipCount += shipsToLand;
		}
		else {
			shipCount -= shipsToLand;
			if (shipCount < 0) {
				inhabitant = invader;
				shipCount *= -1;
				isHomePlanet = false;
			}
		}
		addIncomingShips(shipsToLand * -1, invader);
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