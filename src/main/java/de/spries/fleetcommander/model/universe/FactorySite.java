package de.spries.fleetcommander.model.universe;

public class FactorySite {

	public static class NoFactorySlotsAvailableException extends Exception {
		// Nothing to implement
	}

	public static final int FACTORY_COST = 100;

	private static final int CREDITS_PER_FACTORY_PER_TURN = 75;
	private static final int SHIPS_PER_FACTORY_PER_TURN = 1;
	private static final int FACTORY_SLOTS = 6;
	private int factoryCount = 0;

	public void buildFactory() throws NoFactorySlotsAvailableException {
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

	public int getProducedShipsPerTurn() {
		return factoryCount * SHIPS_PER_FACTORY_PER_TURN;
	}

	public boolean hasAvailableSlots() {
		return factoryCount < FACTORY_SLOTS;
	}

}
