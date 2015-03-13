package de.spries.fleetcommander;

public class ShipFormation {

	private int shipCount;
	private Planet origin;
	private Planet destination;
	private Player commander;

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
				&& commander.equals(existingFormation.commander)) {
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

}
