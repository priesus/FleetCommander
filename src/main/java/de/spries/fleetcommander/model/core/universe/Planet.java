package de.spries.fleetcommander.model.core.universe;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import de.spries.fleetcommander.model.core.Player;
import de.spries.fleetcommander.model.core.common.IllegalActionException;

public class Planet implements HasCoordinates {

	private static final int HOME_PLANET_STARTING_SHIPS = 6;

	private int id;
	private final int x;
	private final int y;
	private boolean isHomePlanet;
	private Player inhabitant;
	private float shipCount;
	private Map<Player, Integer> incomingShipsPerPlayer;
	private Set<Player> knownAsEnemyPlanetBy;

	private FactorySite factorySite = new FactorySite();

	private TurnEventBus turnEventBus;

	public Planet(int x, int y) {
		this(x, y, null);
	}

	public Planet(int x, int y, Player inhabitant) {
		this.x = x;
		this.y = y;
		incomingShipsPerPlayer = new HashMap<>();
		knownAsEnemyPlanetBy = new HashSet<>();
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

	@Override
	public int getX() {
		return x;
	}

	@Override
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
		if (inhabitant != null) {
			shipCount += factorySite.getProducedShipsPerTurn();
			inhabitant.addCredits(factorySite.getProducedCreditsPerTurn());
		}
	}

	public void buildFactory(Player player) {
		if (!canBuildFactory(player)) {
			throw new IllegalActionException("You cannot build a factory right now");
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
			throw new IllegalActionException("You can only send ships from your own planets!");
		}
		if (shipsToSend > shipCount) {
			throw new IllegalActionException("You don't have that many ships to send!");
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
			turnEventBus.fireConqueredUninhabitedPlanet(invader);
			inhabitant = invader;
			shipCount += shipsToLand;
		}
		else if (isInhabitedBy(invader)) {
			shipCount += shipsToLand;
		}
		else {
			// invasion
			shipCount -= shipsToLand;
			if (shipCount < 0) {
				// Successful invasion
				turnEventBus.fireLostPlanet(inhabitant);
				turnEventBus.fireConqueredEnemyPlanet(invader);
				knownAsEnemyPlanetBy.add(inhabitant);
				inhabitant = invader;
				shipCount *= -1;
				isHomePlanet = false;
				knownAsEnemyPlanetBy.remove(invader);
			}
			else {
				// Invaders defeated
				turnEventBus.fireDefendedPlanet(inhabitant);
				turnEventBus.fireLostShipFormation(invader);
				knownAsEnemyPlanetBy.add(invader);
			}
		}
		addIncomingShips(shipsToLand * -1, invader);
	}

	public boolean isKnownAsEnemyPlanet(Player viewingPlayer) {
		return knownAsEnemyPlanetBy.contains(viewingPlayer);
	}

	public void setEventBus(TurnEventBus turnEventBus) {
		this.turnEventBus = turnEventBus;
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

	public static Planet findById(List<Planet> planets, int planetId) {
		return planets.stream().filter(p -> p.getId() == planetId).findFirst().get();
	}

	public static Optional<Planet> filterHomePlanet(List<Planet> planets, Player player) {
		return planets.stream().filter(p -> p.isHomePlanetOf(player)).findFirst();
	}

	public static List<Planet> filterHomePlanets(List<Planet> allPlanets) {
		return allPlanets.stream().filter(p -> p.isHomePlanet()).collect(Collectors.toList());
	}

	public void handleDefeatedPlayer(Player defeatedPlayer) {
		knownAsEnemyPlanetBy.remove(defeatedPlayer);
		incomingShipsPerPlayer.remove(defeatedPlayer);
		if (defeatedPlayer.equals(inhabitant)) {
			shipCount = 0;
			inhabitant = null;
		}
	}
}