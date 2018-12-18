package de.spries.fleetcommander.model.facade;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import de.spries.fleetcommander.model.core.Player;
import de.spries.fleetcommander.model.core.universe.FactorySite;
import de.spries.fleetcommander.model.core.universe.HasCoordinates;
import de.spries.fleetcommander.model.core.universe.Planet;

public class PlayerSpecificPlanet implements HasCoordinates {

	private Planet originalPlanet;
	private Player viewingPlayer;

	public PlayerSpecificPlanet(Planet originalPlanet, Player viewingPlayer) {
		this.originalPlanet = originalPlanet;
		this.viewingPlayer = viewingPlayer;
	}

	public int getId() {
		return originalPlanet.getId();
	}

	@Override
	public int getX() {
		return originalPlanet.getX();
	}

	@Override
	public int getY() {
		return originalPlanet.getY();
	}

	public String getPlanetClass() {
		return isInhabitedByMe() ? originalPlanet.getPlanetClass().name() : "?";
	}

	public boolean isMyHomePlanet() {
		return originalPlanet.isHomePlanetOf(viewingPlayer);
	}

	public boolean isInhabitedByMe() {
		return originalPlanet.isInhabitedBy(viewingPlayer);
	}

	public boolean isKnownAsEnemyPlanet() {
		return originalPlanet.isKnownAsEnemyPlanet(viewingPlayer);
	}

	public boolean isUnderAttack() {
		if (isInhabitedByMe()) {
			return originalPlanet.isUnderAttack();
		}
		return false;
	}

	public boolean isJustInhabited() {
		if (isInhabitedByMe()) {
			return originalPlanet.isJustInhabited();
		}
		return false;
	}

	public boolean canBuildFactory() {
		return originalPlanet.canBuildFactory(viewingPlayer);
	}

	public int getShipCount() {
		if (isInhabitedByMe()) {
			return originalPlanet.getShipCount();
		}
		return 0;
	}

	public int getIncomingShipCount() {
		return originalPlanet.getIncomingShipCount(viewingPlayer);
	}

	public FactorySite getFactorySite() {
		if (isInhabitedByMe()) {
			return originalPlanet.getFactorySite();
		}
		return null;
	}

	public void buildFactory() {
		originalPlanet.buildFactory(viewingPlayer);
	}

	public void changeProductionFocus(int focus) {
		originalPlanet.setProductionFocus(focus, viewingPlayer);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	public static List<PlayerSpecificPlanet> filterMyPlanets(List<PlayerSpecificPlanet> allPlanets) {
		return allPlanets.stream().filter(p -> p.isInhabitedByMe())
				.collect(Collectors.toList());
	}

	protected static PlayerSpecificPlanet convert(Planet planet, Player viewingPlayer) {
		return new PlayerSpecificPlanet(planet, viewingPlayer);
	}

	protected static List<PlayerSpecificPlanet> convert(List<Planet> planets, Player viewingPlayer) {
		return planets.stream().map(p -> convert(p, viewingPlayer))
				.collect(Collectors.toList());
	}
}
