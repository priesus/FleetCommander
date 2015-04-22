package de.spries.fleetcommander.model.core;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.spries.fleetcommander.model.core.universe.Planet;
import de.spries.fleetcommander.model.core.universe.Universe;

public class ComputerPlayer extends Player {

	private static final Logger LOGGER = LogManager.getLogger(ComputerPlayer.class.getName());

	public ComputerPlayer(String name) {
		super(name);
	}

	@Override
	public void notifyNewTurn(Game game) {
		super.notifyNewTurn(game);

		try {
			Universe universe = game.getUniverse();
			Planet homePlanet = universe.getHomePlanetOf(this);
			List<Planet> allPlanets = universe.getPlanets();
			List<Planet> myPlanets = Planet.filterByInhabitant(allPlanets, this);
			List<Planet> enemyPlanets = allPlanets.stream()
					.filter(p -> p.isKnownAsEnemyPlanet(this))
					.collect(Collectors.toList());

			//TODO refactor into factory building strategy

			myPlanets = Planet.sortByDistance(myPlanets, homePlanet);

			for (Planet planet : myPlanets) {
				while (planet.canBuildFactory(this)) {
					planet.buildFactory(this);
				}
			}

			//TODO refactor into fleet coordination strategy

			if (enemyPlanets.isEmpty()) {
				List<Planet> uninvadedPlanets = allPlanets.stream()
						.filter(p -> !p.isInhabitedBy(this) && p.getIncomingShipCount(this) == 0)
						.collect(Collectors.toList());
				uninvadedPlanets = Planet.sortByDistance(uninvadedPlanets, homePlanet);
				List<Planet> planetsToInvade = uninvadedPlanets.subList(0, homePlanet.getShipCount());
				for (Planet planet : planetsToInvade) {
					universe.sendShips(1, homePlanet, planet, this);
				}
			}
			else {
				Planet someEnemyPlanet = enemyPlanets.get(0);
				for (Planet planet : myPlanets) {
					int ships = planet.getShipCount();
					if (ships > 0) {
						universe.sendShips(ships, planet, someEnemyPlanet, this);
					}
				}
			}

		} catch (Exception e) {
			// Just end the turn (still it shouldn't happen)
			String msg = String.format("Game %d: Computer player '%s' caused an exception: ", game.getId(), getName());
			LOGGER.warn(msg, e);
		}

		game.endTurn(this);
	}

}
