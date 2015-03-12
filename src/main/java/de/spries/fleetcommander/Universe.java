package de.spries.fleetcommander;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import de.spries.fleetcommander.Planet.NotPlayersOwnPlanetException;

public class Universe {

	private final List<Planet> planets;
	private List<ShipFormation> travellingShipFormations;

	protected Universe(List<Planet> planets) {
		if (CollectionUtils.isEmpty(planets)) {
			throw new IllegalArgumentException("List of planets required");
		}
		if (!planetsHaveDistinctLocations(planets)) {
			throw new IllegalArgumentException("planets must all have a distinct position: " + planets);
		}
		this.planets = Collections.unmodifiableList(planets);
		travellingShipFormations = new ArrayList<>();
	}

	public List<Planet> getPlanets() {
		return planets;
	}

	public List<Planet> getPlanetsInhabitedBy(Player player) {
		return planets.parallelStream().filter((p) -> p.isInhabitedBy(player)).collect(Collectors.toList());
	}

	public List<Planet> getUninhabitedPlanets() {
		return planets.parallelStream().filter((p) -> !p.isInhabited()).collect(Collectors.toList());
	}

	public Planet getHomePlanetOf(Player player) {
		Optional<Planet> homePlanet = planets.parallelStream().filter((p) -> p.isHomePlanetOf(player)).findFirst();
		if (!homePlanet.isPresent()) {
			throw new IllegalStateException("player " + player + " has no home planet");
		}
		return homePlanet.get();
	}

	public void runFactoryCycle() {
		for (Planet planet : planets) {
			planet.runFactoryCycle();
		}
	}

	private static boolean planetsHaveDistinctLocations(List<Planet> planets) {
		for (int i = 0; i < planets.size() - 1; i++) {
			for (int j = i + 1; j < planets.size(); j++) {
				Planet p1 = planets.get(i);
				Planet p2 = planets.get(j);
				if (p1.distanceTo(p2) == 0) {
					return false;
				}
			}
		}
		return true;
	}

	public void sendShips(int shipCount, Planet origin, Planet destination, Player player)
			throws NotPlayersOwnPlanetException, NotEnoughShipsException {
		if (!planets.contains(origin) || !planets.contains(destination)) {
			throw new IllegalArgumentException("origin & destination must be contained in universe");
		}
		if (!origin.isInhabitedBy(player)) {
			throw new NotPlayersOwnPlanetException();
		}
		if (shipCount > origin.getShipCount()) {
			throw new NotEnoughShipsException();
		}

		if (destination.equals(origin)) {
			return;
		}

		ShipFormation shipFormation = new ShipFormation(shipCount, origin, destination, player);
		travellingShipFormations.add(shipFormation);
	}

	public void runShipTravellingCycle() {
	}

	public List<ShipFormation> getTravellingShipFormations() {
		return travellingShipFormations;
	}

}