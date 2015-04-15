package de.spries.fleetcommander.model.core.universe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import de.spries.fleetcommander.model.core.Player;

public class UniverseFactory {
	public static final int PLANET_COUNT = 100;
	private static final int MIN_PLANET_OFFSET = -2;
	private static final int MAX_PLANET_OFFSET = 2;
	private static final Random rand = new Random();

	/**
	 * Prototype implementation for testing purposes
	 */
	public static Universe generate(List<Player> players) {
		List<Planet> planets = new ArrayList<>(PLANET_COUNT);

		for (int row = 0; row < 10; row++) {
			for (int col = 0; col < 10; col++) {
				int offsetX = randomOffset();
				int offsetY = randomOffset();

				Planet planet = new Planet(col * 10 + 5 + offsetX, row * 10 + 5 + offsetY);
				planets.add(planet);
			}
		}

		Collections.shuffle(planets);

		planets = planets.subList(0, planets.size() / (6 - players.size()));

		int planetId = 0;
		for (Planet planet : planets) {
			planet.setId(planetId++);
		}

		for (int i = 0; i < players.size(); i++) {
			Planet oldUninhabitedPlanet = planets.get(i);
			Planet newHomePlanet = new Planet(oldUninhabitedPlanet.getX(), oldUninhabitedPlanet.getY(), players.get(i));
			newHomePlanet.setId(oldUninhabitedPlanet.getId());
			planets.set(i, newHomePlanet);
		}

		return new Universe(planets);
	}

	private static int randomOffset() {
		return rand.nextInt(MAX_PLANET_OFFSET - MIN_PLANET_OFFSET + 1) + MIN_PLANET_OFFSET;
	}

}