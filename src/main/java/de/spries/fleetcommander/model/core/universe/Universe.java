package de.spries.fleetcommander.model.core.universe;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import de.spries.fleetcommander.model.core.Player;

public class Universe {

	private final List<Planet> planets;
	private Collection<ShipFormation> travellingShipFormations;

	protected Universe(List<Planet> planets) {
		if (CollectionUtils.isEmpty(planets)) {
			throw new IllegalArgumentException("List of planets required");
		}
		this.planets = Collections.unmodifiableList(planets);
		travellingShipFormations = new HashSet<>();
	}

	public List<Planet> getPlanets() {
		return planets;
	}

	public Collection<Planet> getHomePlanets() {
		return planets.parallelStream().filter((p) -> p.isHomePlanet()).collect(Collectors.toList());
	}

	public Planet getHomePlanetOf(Player player) {
		Optional<Planet> homePlanet = planets.parallelStream().filter((p) -> p.isHomePlanetOf(player)).findFirst();
		if (!homePlanet.isPresent()) {
			throw new IllegalStateException("player " + player + " has no home planet");
		}
		return homePlanet.get();
	}

	public void runFactoryProductionCycle() {
		for (Planet planet : planets) {
			planet.runProductionCycle();
		}
	}

	public void runShipTravellingCycle() {
		for (ShipFormation shipFormation : travellingShipFormations) {
			shipFormation.travel();
		}

		travellingShipFormations = travellingShipFormations.parallelStream().filter((s) -> !s.hasArrived())
				.collect(Collectors.toSet());
	}

	public void sendShips(int shipCount, int originPlanetId, int destinationPlanetId, Player player) {
		Planet origin = getPlanetForId(originPlanetId);
		Planet destination = getPlanetForId(destinationPlanetId);
		sendShips(shipCount, origin, destination, player);
	}

	protected void sendShips(int shipCount, Planet origin, Planet destination, Player player) {
		if (!planets.contains(origin) || !planets.contains(destination)) {
			throw new IllegalArgumentException("origin & destination must be contained in universe");
		}

		if (destination.equals(origin)) {
			return;
		}

		origin.sendShipsAway(shipCount, player);
		destination.addIncomingShips(shipCount);

		ShipFormation newShipFormation = new ShipFormation(shipCount, origin, destination, player);
		ShipFormation joinableFormation = getJoinableShipFormation(newShipFormation);

		if (joinableFormation == null) {
			travellingShipFormations.add(newShipFormation);
		} else {
			newShipFormation.join(joinableFormation);
		}
	}

	public Planet getPlanetForId(int planetId) {
		return planets.parallelStream().filter((p) -> p.getId() == planetId).findFirst().get();
	}

	private ShipFormation getJoinableShipFormation(ShipFormation newShipFormation) {
		for (ShipFormation formation : travellingShipFormations) {
			if (newShipFormation.canJoin(formation)) {
				return formation;
			}
		}
		return null;
	}

	public Collection<ShipFormation> getTravellingShipFormations() {
		return travellingShipFormations;
	}

}