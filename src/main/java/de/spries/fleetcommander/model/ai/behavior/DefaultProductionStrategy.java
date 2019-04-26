package de.spries.fleetcommander.model.ai.behavior;

import java.util.List;
import java.util.stream.Collectors;
import de.spries.fleetcommander.model.core.universe.FactorySite;
import de.spries.fleetcommander.model.core.universe.HasCoordinates;
import de.spries.fleetcommander.model.facade.PlayerSpecificPlanet;
import de.spries.fleetcommander.model.facade.PlayerSpecificUniverse;

public class DefaultProductionStrategy implements ProductionStrategy {

	private static final int FULL_SHIP_BUILDING_FOCUS = FactorySite.MAX_PRODUCTION_FOCUS;
	private static final int BALANCED_BUILDING_FOCUS = FULL_SHIP_BUILDING_FOCUS/2;
	private static final int FULL_MONEY_BUILDING_FOCUS = 0;

	@Override
	public void updateProductionFocus(PlayerSpecificUniverse universe, int availablePlayerCredits) {
		List<PlayerSpecificPlanet> allPlanets = universe.getPlanets();
		List<PlayerSpecificPlanet> myPlanets = PlayerSpecificPlanet.filterMyPlanets(allPlanets);

		myPlanets = HasCoordinates.sortByDistanceAsc(myPlanets, universe.getHomePlanet());

		int openFactorySlots = (int) myPlanets.stream().filter(p->p.getFactorySite().hasAvailableSlots()).count();
		int unknownPlanets = (int) allPlanets.stream().filter(p -> !p.isInhabitedByMe() && !p.isKnownAsEnemyPlanet()).count();

		int potentialOpenFactorySlots = openFactorySlots + unknownPlanets * FactorySite.FACTORY_SLOTS;
		int maxFactoryCost = potentialOpenFactorySlots * FactorySite.FACTORY_COST;

		if (potentialOpenFactorySlots == 0 || availablePlayerCredits > maxFactoryCost) {
			// There are no more potential factory slots --> focus on attack
			myPlanets.forEach(p->p.changeProductionFocus(FULL_SHIP_BUILDING_FOCUS));
		}
		else {
			myPlanets.forEach(p->p.changeProductionFocus(BALANCED_BUILDING_FOCUS));

			myPlanets.stream()
					.filter(p1 -> p1.getShipCount() > 5)
					.collect(Collectors.toList()).forEach(p->p.changeProductionFocus(FULL_MONEY_BUILDING_FOCUS));
		}
	}
}
