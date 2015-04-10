package de.spries.fleetcommander.model.universe;

import de.spries.fleetcommander.model.common.IllegalActionException;

public class FactorySite {

	public static class NoFactorySlotsAvailableException extends IllegalActionException {
		// Nothing to implement
	}

	public static final int FACTORY_COST = 100;

	private static final int CREDITS_PER_FACTORY_PER_TURN = 20;
	private static final float SHIPS_PER_FACTORY_PER_TURN = 0.35f;
	private static final int FACTORY_SLOTS = 6;
	private int factoryCount = 0;

	protected void buildFactory() {
		if (FACTORY_SLOTS == factoryCount) {
			throw new NoFactorySlotsAvailableException();
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
		return factoryCount * CREDITS_PER_FACTORY_PER_TURN;
	}

	public float getProducedShipsPerTurn() {
		return factoryCount * SHIPS_PER_FACTORY_PER_TURN;
	}

	public boolean hasAvailableSlots() {
		return factoryCount < FACTORY_SLOTS;
	}

	public int getAvailableSlots() {
		return FACTORY_SLOTS - factoryCount;
	}

}
