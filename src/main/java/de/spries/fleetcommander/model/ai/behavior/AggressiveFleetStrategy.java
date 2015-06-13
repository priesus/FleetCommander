package de.spries.fleetcommander.model.ai.behavior;

import java.util.List;
import java.util.stream.Collectors;

import de.spries.fleetcommander.model.core.universe.HasCoordinates;
import de.spries.fleetcommander.model.facade.PlayerSpecificPlanet;
import de.spries.fleetcommander.model.facade.PlayerSpecificUniverse;

public class AggressiveFleetStrategy implements FleetStrategy {

	@Override
	public void sendShips(PlayerSpecificUniverse universe) {
		PlayerSpecificPlanet homePlanet = universe.getHomePlanet();
		List<PlayerSpecificPlanet> allPlanets = universe.getPlanets();
		List<PlayerSpecificPlanet> myPlanets = PlayerSpecificPlanet.filterMyPlanets(allPlanets);
		List<PlayerSpecificPlanet> enemyPlanets = allPlanets.stream()
				.filter(p -> p.isKnownAsEnemyPlanet())
				.collect(Collectors.toList());

		if (enemyPlanets.isEmpty()) {
			List<PlayerSpecificPlanet> unknownPlanets = allPlanets.stream()
					.filter(p -> !p.isInhabitedByMe() && p.getIncomingShipCount() == 0)
					.collect(Collectors.toList());
			unknownPlanets = HasCoordinates.sortByDistanceAsc(unknownPlanets, homePlanet);

			int numPlanetsToInvade = Math.min(unknownPlanets.size(), homePlanet.getShipCount());
			List<PlayerSpecificPlanet> planetsToInvade = unknownPlanets.subList(0, numPlanetsToInvade);
			for (PlayerSpecificPlanet unknownPlanet : planetsToInvade) {
				universe.sendShips(1, homePlanet.getId(), unknownPlanet.getId());
			}
		}
		else {
			PlayerSpecificPlanet someEnemyPlanet = enemyPlanets.get(0);
			for (PlayerSpecificPlanet planet : myPlanets) {
				int ships = planet.getShipCount();
				if (ships > 0) {
					universe.sendShips(ships, planet.getId(), someEnemyPlanet.getId());
				}
			}
		}
	}

}
