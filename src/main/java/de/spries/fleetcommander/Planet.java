package de.spries.fleetcommander;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Planet {

	private final int coordinateX;
	private final int coordinateY;
	private Player inhabitant;
	private boolean isHomePlanet;

	/**
	 * Regular (yet uninhabited) planed
	 */
	public Planet(int x, int y) {
		this(x, y, null);
	}

	/**
	 * Home planet of a player
	 */
	public Planet(int x, int y, Player inhabitant) {
		coordinateX = x;
		coordinateY = y;
		this.inhabitant = inhabitant;
		isHomePlanet = true;
	}

	public Player getInhabitant() {
		return inhabitant;
	}

	public boolean isHomePlanetOf(Player player) {
		if (isHomePlanet && player.equals(inhabitant)) {
			return true;
		}
		return false;
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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.NO_FIELD_NAMES_STYLE);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
}