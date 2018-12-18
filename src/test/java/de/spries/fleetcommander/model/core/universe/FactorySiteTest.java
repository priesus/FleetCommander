package de.spries.fleetcommander.model.core.universe;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import de.spries.fleetcommander.model.core.common.IllegalActionException;

public class FactorySiteTest {

	private static final float SHIPS_PER_FACTORY_PER_TURN = PlanetClass.B.getShipsPerFactoryPerTurn();
	private static final int CREDITS_PER_FACTORY_PER_TURN = PlanetClass.B.getCreditsPerFactoryPerTurn();
	private FactorySite factorySite;
	private FactorySite maxedOutFactorySite;

	@Before
	public void setUp() throws Exception {
		factorySite = new FactorySite(PlanetClass.B);
		maxedOutFactorySite = new FactorySite(PlanetClass.B);
		for (int i = 0; i < factorySite.getFactorySlotCount(); i++) {
			maxedOutFactorySite.buildFactory();
		}
	}

	@Test(expected = IllegalActionException.class)
	public void cannotBuildMoreFactoriesThanSlotsAvailable() throws Exception {
		maxedOutFactorySite.buildFactory();
	}

	@Test
	public void maxedOutFactorySiteHasNoMoreSlotsAvailable() throws Exception {
		for (int i = 0; i < 6; i++) {
			assertThat(factorySite.hasAvailableSlots(), is(true));
			factorySite.buildFactory();
		}

		assertThat(factorySite.hasAvailableSlots(), is(false));
	}

	@Test
	public void buildingFactoriesDecreasesAvailableSlots() throws Exception {
		for (int i = 6; i > 0; i--) {
			assertThat(factorySite.getAvailableSlots(), is(i));
			factorySite.buildFactory();
		}

		assertThat(factorySite.getAvailableSlots(), is(0));
	}

	@Test
	public void emptyFactorySiteHasNoFactories() throws Exception {
		assertThat(factorySite.getFactoryCount(), is(0));
	}

	@Test
	public void factoryCountIncreasesWithEachBuiltFactory() throws Exception {
		for (int i = 0; i < 6; i++) {
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
		assertThat(factorySite.getProducedShipsPerTurn(), is(0f));
	}

	@Test
	public void factoryIncreasesCreditsProduction() throws Exception {
		factorySite.buildFactory();
		assertThat(factorySite.getProducedCreditsPerTurn(), is(greaterThan(0)));
	}

	@Test
	public void factoryIncreasesShipProduction() throws Exception {
		factorySite.buildFactory();
		assertThat(factorySite.getProducedShipsPerTurn(), is(greaterThan(0f)));
	}

	@Test
	public void initialProductionFocusIsBalanced50Percent() throws Exception {
		assertThat(factorySite.getShipProductionFocus(), is(10));
	}

	@Test
	public void fullProductionFocusOnShipsProducesShipsOnly() throws Exception {
		factorySite.buildFactory();
		factorySite.setShipProductionFocus(20);
		assertThat(factorySite.getProducedShipsPerTurn(), is(SHIPS_PER_FACTORY_PER_TURN));
		assertThat(factorySite.getProducedCreditsPerTurn(), is(0));
	}

	@Test
	public void fullProductionFocusOnCreditsProducesCreditsOnly() throws Exception {
		factorySite.buildFactory();
		factorySite.setShipProductionFocus(0);
		assertThat(factorySite.getProducedCreditsPerTurn(), is(CREDITS_PER_FACTORY_PER_TURN));
		assertThat(factorySite.getProducedShipsPerTurn(), is(0f));
	}

	@Test
	public void balancedProductionFocusProducesBothShipsAndCredits() throws Exception {
		factorySite.buildFactory();
		factorySite.setShipProductionFocus(10);
		assertThat(factorySite.getProducedCreditsPerTurn(), is(CREDITS_PER_FACTORY_PER_TURN / 2));
		assertThat(factorySite.getProducedShipsPerTurn(), is(SHIPS_PER_FACTORY_PER_TURN / 2));
	}

	@Test
	public void differentPlanetClassProducesDifferentResources() throws Exception {
		FactorySite factorySite = new FactorySite(PlanetClass.P);
		factorySite.buildFactory();
		factorySite.setShipProductionFocus(10);
		assertThat(factorySite.getProducedCreditsPerTurn(), is(PlanetClass.P.getCreditsPerFactoryPerTurn() / 2));
		assertThat(factorySite.getProducedShipsPerTurn(), is(PlanetClass.P.getShipsPerFactoryPerTurn() / 2));
	}

	@Test
	public void shipProductionFocusProducesMoreShipsThanCredits() throws Exception {
		factorySite.buildFactory();
		factorySite.setShipProductionFocus(15);
		assertThat(factorySite.getProducedCreditsPerTurn(), is(CREDITS_PER_FACTORY_PER_TURN / 4 * 1));
		assertThat(factorySite.getProducedShipsPerTurn(), is(SHIPS_PER_FACTORY_PER_TURN / 4 * 3));
	}

	@Test
	public void creditProductionFocusProducesMoreCreditsThanShips() throws Exception {
		factorySite.buildFactory();
		factorySite.setShipProductionFocus(5);
		assertThat(factorySite.getProducedCreditsPerTurn(), is(CREDITS_PER_FACTORY_PER_TURN / 4 * 3));
		assertThat(factorySite.getProducedShipsPerTurn(), is(SHIPS_PER_FACTORY_PER_TURN / 4 * 1));
	}

	@Test(expected = IllegalActionException.class)
	public void cannotSetNegativeProductionFocus() throws Exception {
		factorySite.setShipProductionFocus(-1);
	}

	@Test(expected = IllegalActionException.class)
	public void cannotSetProductionFocusGreaterThan20() throws Exception {
		factorySite.setShipProductionFocus(21);
	}
}
