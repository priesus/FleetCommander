package de.spries.fleetcommander.model.facade;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import de.spries.fleetcommander.model.core.Player;
import de.spries.fleetcommander.model.core.universe.ShipFormation;
import de.spries.fleetcommander.model.core.universe.Universe;

public class PlayerSpecificUniverse {

	private Universe originalUniverse;
	private Player viewingPlayer;
	private List<PlayerSpecificPlanet> planets;
	private PlayerSpecificPlanet homePlanet;

	public PlayerSpecificUniverse(Universe originalUniverse, Player viewingPlayer) {
		this.originalUniverse = originalUniverse;
		this.viewingPlayer = viewingPlayer;

		planets = PlayerSpecificPlanet.convert(originalUniverse.getPlanets(), viewingPlayer);
		homePlanet = PlayerSpecificPlanet.convert(originalUniverse.getHomePlanetOf(viewingPlayer), viewingPlayer);
	}

	public List<PlayerSpecificPlanet> getPlanets() {
		return planets;
	}

	public PlayerSpecificPlanet getHomePlanet() {
		return homePlanet;
	}

	public void sendShips(int shipCount, int originPlanetId, int destinationPlanetId) {
		originalUniverse.sendShips(shipCount, originPlanetId, destinationPlanetId, viewingPlayer);
	}

	public Collection<ShipFormation> getTravellingShipFormations() {
		return originalUniverse.getTravellingShipFormations().parallelStream()
				.filter((s) -> s.getCommander().equals(viewingPlayer)).collect(Collectors.toList());
	}

	protected static PlayerSpecificUniverse convert(Universe universe, Player viewingPlayer) {
		return new PlayerSpecificUniverse(universe, viewingPlayer);
	}

}
