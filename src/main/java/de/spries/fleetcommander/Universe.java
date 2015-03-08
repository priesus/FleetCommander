package de.spries.fleetcommander;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Universe {

	private final List<Planet> planets;

	public Universe(List<Planet> planets) {
		this.planets = Collections.unmodifiableList(planets);
	}

	public List<Planet> getPlanets() {
		return planets;
	}

	/**
	 * Temporary implementation for visualization
	 */
	@Override
	public String toString() {
		int maxX = 0;
		int maxY = 0;
		for (Planet p : planets) {
			if (p.getCoordinateX() > maxX) {
				maxX = p.getCoordinateX();
			}
			if (p.getCoordinateY() > maxY) {
				maxY = p.getCoordinateY();
			}
		}
		char universe[][] = new char[maxX + 1][maxY + 1];
		for (Planet p : planets) {
			universe[p.getCoordinateX()][p.getCoordinateY()] = 'X';
		}

		StringBuilder sb = new StringBuilder(maxX * (maxY + 1));

		for (char[] line : universe) {
			sb.append(Arrays.toString(line));
			sb.append('\n');
		}

		return sb.toString();
	}

}