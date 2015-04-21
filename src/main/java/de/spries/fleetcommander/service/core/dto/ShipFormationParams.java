package de.spries.fleetcommander.service.core.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}