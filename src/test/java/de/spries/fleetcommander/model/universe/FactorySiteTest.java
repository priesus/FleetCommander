package de.spries.fleetcommander.model.universe;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import de.spries.fleetcommander.model.universe.FactorySite.NoFactorySlotsAvailableException;

public class FactorySiteTest {

	private FactorySite factorySite;
	private FactorySite maxedOutFactorySite;

	@Before
	public void setUp() throws Exception {
		factorySite = new FactorySite();
		maxedOutFactorySite = new FactorySite();
		for (int i = 0; i < factorySite.getFactorySlotCount(); i++) {
			maxedOutFactorySite.buildFactory();
		}
	}

	@Test(expected = NoFactorySlotsAvailableException.class)
	public void cannotBuildMoreFactoriesThanSlotsAvailable() throws Exception {
		maxedOutFactorySite.buildFactory();
	}

	@Test
	public void mayedOutFactorySiteHasNoMoreSlotsAvailable() throws Exception {
		for (int i = 0; i < factorySite.getFactorySlotCount(); i++) {
			assertThat(factorySite.hasAvailableSlots(), is(true));
			factorySite.buildFactory();
		}

		assertThat(factorySite.hasAvailableSlots(), is(false));
	}

	@Test
	public void emptyFactorySiteHasNoFactories() throws Exception {
		assertThat(factorySite.getFactoryCount(), is(0));
	}

	@Test
	public void factoryCountIncreasesWithEachBuiltFactory() throws Exception {
		for (int i = 0; i < factorySite.getFactorySlotCount(); i++) {
			factorySite.buildFactory();
			assertThat(factorySite.getFactoryCount(), is(i + 1));
		}
	}

	@Test
	public void emptyFactorySiteProducesNoCredits() throws Exception {
		assertThat(factorySite.getProducedCreditsPerTurn(), is(0));
	}

	@Test
	public void emptyFactorySiteProducesNoShips() throws Exception {
		assertThat(factorySite.getProducedShipsPerTurn(), is(0));
	}

	@Test
	public void factoryIncreasesCreditsProduction() throws Exception {
		factorySite.buildFactory();
		assertThat(factorySite.getProducedCreditsPerTurn(), is(greaterThan(0)));
	}

	@Test
	public void factoryIncreasesShipProduction() throws Exception {
		factorySite.buildFactory();
		assertThat(factorySite.getProducedShipsPerTurn(), is(greaterThan(0)));
	}
}
