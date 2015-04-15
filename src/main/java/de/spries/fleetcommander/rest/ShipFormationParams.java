package de.spries.fleetcommander.rest;

public class ShipFormationParams {
	private int shipCount;
	private int originPlanetId;
	private int destinationPlanetId;

	public int getShipCount() {
		return shipCount;
	}

	public void setShipCount(int shipCount) {
		this.shipCount = shipCount;
	}

	public int getOriginPlanetId() {
		return originPlanetId;
	}

	public void setOriginPlanetId(int originPlanetId) {
		this.originPlanetId = originPlanetId;
	}

	public int getDestinationPlanetId() {
		return destinationPlanetId;
	}

	public void setDestinationPlanetId(int destinationPlanetId) {
		this.destinationPlanetId = destinationPlanetId;
	}
}