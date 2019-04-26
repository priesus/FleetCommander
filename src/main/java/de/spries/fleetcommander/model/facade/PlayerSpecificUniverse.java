package de.spries.fleetcommander.model.facade;

import java.util.Collection;
import java.util.List;

import de.spries.fleetcommander.model.core.Player;
import de.spries.fleetcommander.model.core.universe.Planet;
import de.spries.fleetcommander.model.core.universe.ShipFormation;
import de.spries.fleetcommander.model.core.universe.Universe;

import static de.spries.fleetcommander.model.facade.TestMode.TEST_MODE;

public class PlayerSpecificUniverse {

	private Universe originalUniverse;
	private Player viewingPlayer;

	public PlayerSpecificUniverse(Universe originalUniverse, Player viewingPlayer) {
		this.originalUniverse = originalUniverse;
		this.viewingPlayer = viewingPlayer;
	}

	public List<PlayerSpecificPlanet> getPlanets() {
		return PlayerSpecificPlanet.convert(originalUniverse.getPlanets(), viewingPlayer);
	}

	public PlayerSpecificPlanet getPlanet(int planetId) {
		return PlayerSpecificPlanet.convert(originalUniverse.getPlanetForId(planetId), viewingPlayer);
	}

	public PlayerSpecificPlanet getHomePlanet() {
		Planet homePlanet = originalUniverse.getHomePlanetOf(viewingPlayer);
		if (homePlanet != null) {
			return PlayerSpecificPlanet.convert(homePlanet, viewingPlayer);
		}
		return null;
	}

	public void sendShips(int shipCount, int originPlanetId, int destinationPlanetId) {
		originalUniverse.sendShips(shipCount, originPlanetId, destinationPlanetId, viewingPlayer);
	}

	public Collection<ShipFormation> getTravellingShipFormations() {
		if(TEST_MODE)
			return originalUniverse.getTravellingShipFormations();
		return ShipFormation.filterByCommander(originalUniverse.getTravellingShipFormations(), viewingPlayer);
	}

	protected static PlayerSpecificUniverse convert(Universe universe, Player viewingPlayer) {
		return new PlayerSpecificUniverse(universe, viewingPlayer);
	}

}
