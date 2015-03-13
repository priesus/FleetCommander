package de.spries.fleetcommander.model.universe;

import java.util.ArrayList;
import java.util.List;

import de.spries.fleetcommander.model.player.Player;


public class UniverseGenerator {

	public static Universe generate(int planetCount, List<Player> players) {
		List<Planet> planets = new ArrayList<>();
	
		for (int i = 0; i < players.size(); i++) {
			Planet planet = new Planet(0, i, players.get(i));
			planets.add(planet);
		}
	
		for (int i = players.size(); i < planetCount; i++) {
			Planet planet = new Planet(0, i);
			planets.add(planet);
		}
	
		return new Universe(planets);
	}

}