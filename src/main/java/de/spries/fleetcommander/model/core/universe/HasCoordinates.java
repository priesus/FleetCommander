package de.spries.fleetcommander.model.core.universe;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public interface HasCoordinates {

	int getX();

	int getY();

	default double distanceTo(HasCoordinates other) {
		int distanceX = getX() - other.getX();
		int distanceY = getY() - other.getY();
		return Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));
	}

	static <T extends HasCoordinates> List<T> sortByDistanceAsc(List<T> objects, T referenceObject) {
		return objects.stream().sorted(getComparatorForClosestFirst(referenceObject)).collect(Collectors.toList());
	}

	static <T extends HasCoordinates> Comparator<T> getComparatorForClosestFirst(T referenceObject) {
		return Comparator.comparingDouble(o -> o.distanceTo(referenceObject));
	}
}
