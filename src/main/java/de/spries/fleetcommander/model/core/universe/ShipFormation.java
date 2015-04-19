package de.spries.fleetcommander.model.core.universe;

import java.util.Collection;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import de.spries.fleetcommander.model.core.Player;

public class ShipFormation {

	public static final int DISTANCE_PER_TURN = 8;

	private int shipCount;
	private Planet origin;
	private Planet destination;
	private Player commander;
	private int distanceTravelled = 0;
	private final double distanceOverall;

	public ShipFormation(int shipCount, Planet origin, Planet destination, Player commander) {
		if (shipCount <= 0) {
			throw new IllegalArgumentException("Must send positive number of ships");
		}
		if (origin == null || destination == null || commander == null) {
			throw new IllegalArgumentException("all parameters must be non-null");
		}
		this.shipCount = shipCount;
		this.origin = origin;
		this.destination = destination;
		this.commander = commander;
		distanceOverall = origin.distanceTo(destination);
	}

	public int getShipCount() {
		return shipCount;
	}

	public Planet getOrigin() {
		return origin;
	}

	public Planet getDestination() {
		return destination;
	}

	public Player getCommander() {
		return commander;
	}

	public boolean canJoin(ShipFormation existingFormation) {
		if (origin.equals(existingFormation.origin) && destination.equals(existingFormation.destination)
				&& commander.equals(existingFormation.commander)
				&& distanceTravelled == existingFormation.distanceTravelled) {
			return true;
		}
		return false;
	}

	public void join(ShipFormation existingFormation) {
		if (!canJoin(existingFormation)) {
			throw new IllegalArgumentException("cannot join this formation");
		}
		existingFormation.shipCount += shipCount;
		shipCount = 0;
	}

	public void travel() {
		distanceTravelled += DISTANCE_PER_TURN;
		if (hasArrived()) {
			landOnDestination();
		}
	}

	protected void landOnDestination() {
		destination.landShips(shipCount, commander);
		shipCount = 0;
	}

	public boolean hasArrived() {
		return distanceTravelled >= distanceOverall;
	}

	public int getDistanceTravelled() {
		return distanceTravelled;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	public double getPositionX() {
		return origin.getX() + (destination.getX() - origin.getX()) * distanceTravelled / distanceOverall;
	}

	public double getPositionY() {
		return origin.getY() + (destination.getY() - origin.getY()) * distanceTravelled / distanceOverall;
	}

	public static Collection<ShipFormation> filterByCommander(Collection<ShipFormation> shipFormations, Player commander) {
		return shipFormations.parallelStream()
				.filter((s) -> s.getCommander().equals(commander)).collect(Collectors.toList());
	}

}
