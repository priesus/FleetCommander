package de.spries.fleetcommander.model.ai.behavior;

import java.util.List;

import de.spries.fleetcommander.model.core.universe.HasCoordinates;
import de.spries.fleetcommander.model.facade.PlayerSpecificPlanet;
import de.spries.fleetcommander.model.facade.PlayerSpecificUniverse;

public class DefaultBuildingStrategy implements BuildingStrategy {

	@Override
	public void buildFactories(PlayerSpecificUniverse universe) {
		List<PlayerSpecificPlanet> allPlanets = universe.getPlanets();
		List<PlayerSpecificPlanet> myPlanets = PlayerSpecificPlanet.filterMyPlanets(allPlanets);

		myPlanets = HasCoordinates.sortByDistanceAsc(myPlanets, universe.getHomePlanet());

		for (PlayerSpecificPlanet planet : myPlanets) {
			while (planet.canBuildFactory()) {
				planet.buildFactory();
			}
		}
	}

}
