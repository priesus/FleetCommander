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
		Comparator<T> CLOSE_OBJECTS_FIRST = new Comparator<T>() {

			@Override
			public int compare(HasCoordinates o1, HasCoordinates o2) {
				return Double.compare(o1.distanceTo(referenceObject), o2.distanceTo(referenceObject));
			}
		};

		return objects.stream().sorted(CLOSE_OBJECTS_FIRST).collect(Collectors.toList());
	}

}
