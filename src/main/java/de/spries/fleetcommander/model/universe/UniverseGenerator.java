package de.spries.fleetcommander.model.universe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.spries.fleetcommander.model.player.Player;

public class UniverseGenerator {
	public static final int PLANET_COUNT = 100;

	/**
	 * Prototype implementation for testing purposes
	 */
	public static Universe generate(List<Player> players) {
		List<Planet> planets = new ArrayList<>(PLANET_COUNT);

		for (int row = 0; row < 10; row++) {
			for (int col = 0; col < 10; col++) {
				Planet planet = new Planet(col * 10 + 5, row * 10 + 5, null);
				planets.add(planet);
			}
		}

		Collections.shuffle(planets);
		for (int i = 0; i < players.size(); i++) {
			Planet oldUninhabitedPlanet = planets.get(i);
			Planet newHomePlanet = new Planet(oldUninhabitedPlanet.getX(), oldUninhabitedPlanet.getY(), players.get(i));
			planets.set(i, newHomePlanet);
		}

		return new Universe(planets);
	}

}