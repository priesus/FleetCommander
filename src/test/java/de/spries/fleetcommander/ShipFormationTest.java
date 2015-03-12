package de.spries.fleetcommander;

import static org.mockito.Mockito.mock;

import org.junit.Test;

public class ShipFormationTest {

	private static final Player JOHN = mock(Player.class);
	private static final Planet PLANET = mock(Planet.class);

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

}
