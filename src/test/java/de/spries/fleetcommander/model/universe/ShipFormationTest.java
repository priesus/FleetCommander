package de.spries.fleetcommander.model.universe;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import de.spries.fleetcommander.model.player.Player;
import de.spries.fleetcommander.model.universe.Planet;
import de.spries.fleetcommander.model.universe.ShipFormation;

public class ShipFormationTest {

	private static final Player JOHN = mock(Player.class);
	private static final Player JACK = mock(Player.class);
	private static final Planet PLANET = mock(Planet.class);
	private static final Planet OTHER_PLANET = mock(Planet.class);
	private ShipFormation existingFormation;

	@Before
	public void setUp() {
		existingFormation = new ShipFormation(1, PLANET, OTHER_PLANET, JOHN);
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void shipCountMustBeNonNegative() {
		new ShipFormation(-1, PLANET, PLANET, JOHN);
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void shipCountMustBeNonZero() {
		new ShipFormation(0, PLANET, PLANET, JOHN);
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void originMustBeNonNull() {
		new ShipFormation(1, null, PLANET, JOHN);
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void destinationMustBeNonNull() {
		new ShipFormation(1, PLANET, null, JOHN);
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void commanderMustBeNonNull() {
		new ShipFormation(1, PLANET, PLANET, null);
	}

	@Test
	public void formationCannotJoinIfOriginIsDifferent() {
		ShipFormation newShipFormation = new ShipFormation(1, OTHER_PLANET, OTHER_PLANET, JOHN);
		assertThat(newShipFormation.canJoin(existingFormation), is(false));
	}

	@Test
	public void formationCannotJoinIfDestinationIsDifferent() {
		ShipFormation newShipFormation = new ShipFormation(1, PLANET, PLANET, JOHN);
		assertThat(newShipFormation.canJoin(existingFormation), is(false));
	}

	@Test
	public void formationCannotJoinIfCommanderIsDifferent() {
		ShipFormation newShipFormation = new ShipFormation(1, PLANET, OTHER_PLANET, JACK);
		assertThat(newShipFormation.canJoin(existingFormation), is(false));
	}

	@Test
	public void formationCanJoinIfRouteAndCommanderAreEqual() {
		ShipFormation newShipFormation = new ShipFormation(1, PLANET, OTHER_PLANET, JOHN);
		assertThat(newShipFormation.canJoin(existingFormation), is(true));
	}

	@Test
	public void mergingIncreasesShipCountOfExistingFormation() {
		ShipFormation joiningFormation = new ShipFormation(1, PLANET, OTHER_PLANET, JOHN);
		joiningFormation.join(existingFormation);
		assertThat(existingFormation.getShipCount(), is(2));
	}

	@Test
	public void mergingDecreasesShipCountOfJoiningFormationToZero() {
		ShipFormation joiningFormation = new ShipFormation(1, PLANET, OTHER_PLANET, JOHN);
		joiningFormation.join(existingFormation);
		assertThat(joiningFormation.getShipCount(), is(0));
	}

	@Test
	public void landingOnDestinationTransfersShipsToPlanet() {
		existingFormation.landOnDestination();
		assertThat(existingFormation.getShipCount(), is(0));
		verify(OTHER_PLANET).landShips(1, JOHN);
	}

}
