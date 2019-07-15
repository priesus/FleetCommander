package de.spries.fleetcommander.model.core.universe;

public interface HasCoordinates {

    int getX();

    int getY();

    default double distanceTo(HasCoordinates other) {
        int distanceX = getX() - other.getX();
        int distanceY = getY() - other.getY();
        return Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));
    }
}
