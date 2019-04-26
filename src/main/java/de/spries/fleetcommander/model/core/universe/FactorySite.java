package de.spries.fleetcommander.model.core.universe;

import de.spries.fleetcommander.model.core.common.IllegalActionException;

public class FactorySite {

	public static final int FACTORY_COST = 100;
	public static final int MAX_PRODUCTION_FOCUS = 20;
	public static final int FACTORY_SLOTS = 6;


	private PlanetClass planetClass;
	private int factoryCount = 0;
	private int shipProductionFocus = MAX_PRODUCTION_FOCUS / 2;

	public FactorySite(PlanetClass planetClass) {
		this.planetClass = planetClass;
	}

	protected void buildFactory() {
		if (FACTORY_SLOTS == factoryCount) {
			throw new IllegalActionException("No more space for factories!");
		}
		factoryCount++;
	}

	public int getFactorySlotCount() {
		return FACTORY_SLOTS;
	}

	public int getFactoryCount() {
		return factoryCount;
	}

	public int getProducedCreditsPerTurn() {
		int creditsProductionFocus = MAX_PRODUCTION_FOCUS - shipProductionFocus;
		return factoryCount * planetClass.getCreditsPerFactoryPerTurn() * creditsProductionFocus / MAX_PRODUCTION_FOCUS;
	}

	public float getProducedShipsPerTurn() {
		return factoryCount * planetClass.getShipsPerFactoryPerTurn() * shipProductionFocus / MAX_PRODUCTION_FOCUS;
	}

	public boolean hasAvailableSlots() {
		return factoryCount < FACTORY_SLOTS;
	}

	public int getAvailableSlots() {
		return FACTORY_SLOTS - factoryCount;
	}

	/**
	 * Change the production focus of all factories. Higher values will produce more ships, lower values will produce
	 * more credits.
	 *
	 * @param prodFocus
	 *            Value between 0 and 20 where 20 is full focus on building ships and 0 is full focus on producing
	 *            credits
	 */
	protected void setShipProductionFocus(int prodFocus) {
		if (prodFocus < 0 || prodFocus > MAX_PRODUCTION_FOCUS) {
			throw new IllegalActionException("Production focus of factory sites must be between 0 and "
					+ MAX_PRODUCTION_FOCUS);
		}
		shipProductionFocus = prodFocus;
	}

	/**
	 * The production focus of all factories. Higher values will produce more ships, lower values will produce
	 * more credits.
	 *
	 * Values may range from 0 to 20.
	 */
	public int getShipProductionFocus() {
		return shipProductionFocus;
	}

}
