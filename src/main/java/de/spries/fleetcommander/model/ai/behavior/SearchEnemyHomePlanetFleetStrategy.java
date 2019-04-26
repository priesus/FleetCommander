package de.spries.fleetcommander.model.ai.behavior;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import de.spries.fleetcommander.model.core.universe.HasCoordinates;
import de.spries.fleetcommander.model.facade.PlayerSpecificPlanet;
import de.spries.fleetcommander.model.facade.PlayerSpecificUniverse;

public class SearchEnemyHomePlanetFleetStrategy implements FleetStrategy {

	@Override
	public void sendShips(PlayerSpecificUniverse universe) {
		PlayerSpecificPlanet homePlanet = universe.getHomePlanet();
		List<PlayerSpecificPlanet> allPlanets = universe.getPlanets();
		List<PlayerSpecificPlanet> myPlanets = PlayerSpecificPlanet.filterMyPlanets(allPlanets);
		Optional<PlayerSpecificPlanet> enemyHomePlanet = allPlanets.stream()
				.filter(p -> p.isKnownAsEnemyPlanet() && p.isHomePlanet()).findFirst();
		List<PlayerSpecificPlanet> unknownPlanets = allPlanets.stream()
				.filter(p -> !p.isInhabitedByMe() && !p.isKnownAsEnemyPlanet() && p.getIncomingShipCount() == 0)
				.sorted(HasCoordinates.getComparatorForClosestFirst(homePlanet))
				.collect(Collectors.toList());

		if(enemyHomePlanet.isPresent()) {
			// Full attack on enemy home planet
			for (PlayerSpecificPlanet myPlanet : myPlanets) {
				universe.sendShips(myPlanet.getShipCount(), myPlanet.getId(), enemyHomePlanet.get().getId());
			}
		}
		else {
			// Expand to next closest uninhabited planets
			for (PlayerSpecificPlanet unknownPlanet : unknownPlanets) {
				// send 1 ship from closest of my own planets which has ships > 0
				Optional<PlayerSpecificPlanet> closestOwnPlanetWhichHasShips = myPlanets.stream()
						.sorted(HasCoordinates.getComparatorForClosestFirst(unknownPlanet))
						.filter(p -> p.getShipCount() > 0)
						.findFirst();
				if(!closestOwnPlanetWhichHasShips.isPresent()){
					break;
				}
				universe.sendShips(1, closestOwnPlanetWhichHasShips.get().getId(), unknownPlanet.getId());
			}
		}
	}

}
