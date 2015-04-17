package de.spries.fleetcommander.model.facade;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import de.spries.fleetcommander.model.core.Player;
import de.spries.fleetcommander.model.core.universe.FactorySite;
import de.spries.fleetcommander.model.core.universe.Planet;

public class PlayerSpecificPlanet {

	private Planet originalPlanet;
	private Player viewingPlayer;

	public PlayerSpecificPlanet(Planet originalPlanet, Player viewingPlayer) {
		this.originalPlanet = originalPlanet;
		this.viewingPlayer = viewingPlayer;
	}

	public int getId() {
		return originalPlanet.getId();
	}

	public int getX() {
		return originalPlanet.getX();
	}

	public int getY() {
		return originalPlanet.getY();
	}

	public boolean isMyHomePlanet() {
		return originalPlanet.isHomePlanetOf(viewingPlayer);
	}

	public boolean isInhabitedByMe() {
		return originalPlanet.isInhabitedBy(viewingPlayer);
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

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	protected static PlayerSpecificPlanet convert(Planet planet, Player viewingPlayer) {
		return new PlayerSpecificPlanet(planet, viewingPlayer);
	}

	protected static List<PlayerSpecificPlanet> convert(List<Planet> planets, Player viewingPlayer) {
		return planets.parallelStream().map((p) -> convert(p, viewingPlayer))
				.collect(Collectors.toList());
	}
}
