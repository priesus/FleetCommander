package de.spries.fleetcommander;

import java.util.ArrayList;
import java.util.List;

public class UniverseGenerator {

	private UniverseGenerator() {
		// Hide constructor for utility class
	}

	public static Universe generate(int planetCount) {
		List<Planet> planets = new ArrayList<>();

		for (int i = 0; i < planetCount; i++) {
			Planet planet = new Planet(0, i);
			planets.add(planet);
		}

		return new Universe(planets);
	}

}