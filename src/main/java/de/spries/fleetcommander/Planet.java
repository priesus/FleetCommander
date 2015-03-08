package de.spries.fleetcommander;

public class Planet {

	private final int coordinateX;
	private final int coordinateY;

	public Planet(int x, int y) {
		coordinateX = x;
		coordinateY = y;
	}

	public int getCoordinateX() {
		return coordinateX;
	}

	public int getCoordinateY() {
		return coordinateY;
	}

	public double distanceTo(Planet other) {
		int distanceX = coordinateX - other.getCoordinateX();
		int distanceY = coordinateY - other.getCoordinateY();
		return Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));
	}
}