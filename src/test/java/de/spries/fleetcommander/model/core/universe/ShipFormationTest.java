package de.spries.fleetcommander.model.core.universe;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import de.spries.fleetcommander.model.core.Player;
import de.spries.fleetcommander.model.core.universe.Planet;
import de.spries.fleetcommander.model.core.universe.ShipFormation;

public class ShipFormationTest {

	private static final Player JOHN = mock(Player.class);
	private static final Player JACK = mock(Player.class);
	private ShipFormation existingFormation;
	private Planet originPlanet = mock(Planet.class);
	private Planet closePlanet = mock(Planet.class);
	private Planet moreDistantPlanet = mock(Planet.class);
	private Planet distantPlanet = mock(Planet.class);

	@Before
	public void setUp() {
		originPlanet = mock(Planet.class);
		closePlanet = mock(Planet.class);
		moreDistantPlanet = mock(Planet.class);
		distantPlanet = mock(Planet.class);

		existingFormation = new ShipFormation(1, originPlanet, closePlanet, JOHN);
		doReturn(1.0 * ShipFormation.DISTANCE_PER_TURN).when(originPlanet).distanceTo(closePlanet);
		doReturn(2.0 * ShipFormation.DISTANCE_PER_TURN).when(originPlanet).distanceTo(moreDistantPlanet);
		doReturn(3.0 * ShipFormation.DISTANCE_PER_TURN).when(originPlanet).distanceTo(distantPlanet);
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void shipCountMustBeNonNegative() {
		new ShipFormation(-1, originPlanet, originPlanet, JOHN);
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void shipCountMustBeNonZero() {
		new ShipFormation(0, originPlanet, originPlanet, JOHN);
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void originMustBeNonNull() {
		new ShipFormation(1, null, originPlanet, JOHN);
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void destinationMustBeNonNull() {
		new ShipFormation(1, originPlanet, null, JOHN);
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void commanderMustBeNonNull() {
		new ShipFormation(1, originPlanet, originPlanet, null);
	}

	@Test
	public void formationCannotJoinIfOriginIsDifferent() {
		ShipFormation newShipFormation = new ShipFormation(1, closePlanet, closePlanet, JOHN);
		assertThat(newShipFormation.canJoin(existingFormation), is(false));
	}

	@Test
	public void formationCannotJoinIfDestinationIsDifferent() {
		ShipFormation newShipFormation = new ShipFormation(1, originPlanet, originPlanet, JOHN);
		assertThat(newShipFormation.canJoin(existingFormation), is(false));
	}

	@Test
	public void formationCannotJoinIfCommanderIsDifferent() {
		ShipFormation newShipFormation = new ShipFormation(1, originPlanet, closePlanet, JACK);
		assertThat(newShipFormation.canJoin(existingFormation), is(false));
	}

	@Test
	public void formationCannotJoinIfDistancetravelledIsDifferent() {
		ShipFormation newShipFormation = new ShipFormation(1, originPlanet, closePlanet, JOHN);
		newShipFormation.travel();
		assertThat(newShipFormation.canJoin(existingFormation), is(false));
	}

	@Test
	public void formationCanJoinIfRouteAndCommanderAreEqual() {
		ShipFormation newShipFormation = new ShipFormation(1, originPlanet, closePlanet, JOHN);
		assertThat(newShipFormation.canJoin(existingFormation), is(true));
	}

	@Test
	public void mergingIncreasesShipCountOfExistingFormation() {
		ShipFormation joiningFormation = new ShipFormation(1, originPlanet, closePlanet, JOHN);
		joiningFormation.join(existingFormation);
		assertThat(existingFormation.getShipCount(), is(2));
	}

	@Test
	public void mergingDecreasesShipCountOfJoiningFormationToZero() {
		ShipFormation joiningFormation = new ShipFormation(1, originPlanet, closePlanet, JOHN);
		joiningFormation.join(existingFormation);
		assertThat(joiningFormation.getShipCount(), is(0));
	}

	@Test(expected = IllegalArgumentException.class)
	public void cannotMergeFormationsIfCommanderIsNotEqual() {
		ShipFormation joiningFormation = new ShipFormation(1, originPlanet, closePlanet, JACK);
		joiningFormation.join(existingFormation);
	}

	@Test
	public void landingOnDestinationTransfersShipsToPlanet() {
		existingFormation.landOnDestination();
		assertThat(existingFormation.getShipCount(), is(0));
		verify(closePlanet).landShips(1, JOHN);
	}

	@Test
	public void shipsHaventArrivedBeforeTravelling() throws Exception {
		ShipFormation sf = new ShipFormation(1, originPlanet, closePlanet, JOHN);
		assertThat(sf.hasArrived(), is(false));
	}

	@Test
	public void shortTripLandsAfter1Cycle() throws Exception {
		ShipFormation sf = new ShipFormation(1, originPlanet, closePlanet, JOHN);
		sf.travel();
		verify(closePlanet).landShips(1, JOHN);
		assertThat(sf.hasArrived(), is(true));
	}

	@Test
	public void longerTripEndsAfter2Cycles() throws Exception {
		ShipFormation sf = new ShipFormation(1, originPlanet, moreDistantPlanet, JOHN);

		sf.travel();
		verify(moreDistantPlanet, never()).landShips(1, JOHN);
		assertThat(sf.hasArrived(), is(false));

		sf.travel();
		verify(moreDistantPlanet).landShips(1, JOHN);
		assertThat(sf.hasArrived(), is(true));
	}

	@Test
	public void longTripEndsAfter3Cycles() throws Exception {
		ShipFormation sf = new ShipFormation(1, originPlanet, distantPlanet, JOHN);

		sf.travel();
		verify(distantPlanet, never()).landShips(1, JOHN);

		sf.travel();
		verify(distantPlanet, never()).landShips(1, JOHN);
		assertThat(sf.hasArrived(), is(false));

		sf.travel();
		verify(distantPlanet).landShips(1, JOHN);
		assertThat(sf.hasArrived(), is(true));
	}

	@Test
	public void distanceTravelledIncreasesWithEachCycle() throws Exception {
		ShipFormation sf = new ShipFormation(1, originPlanet, moreDistantPlanet, JOHN);

		sf.travel();
		assertThat(sf.getDistanceTravelled(), is(1 * ShipFormation.DISTANCE_PER_TURN));
		sf.travel();
		assertThat(sf.getDistanceTravelled(), is(2 * ShipFormation.DISTANCE_PER_TURN));
	}

	@Test
	public void formationPositionMovesTowardsDestination() throws Exception {
		doReturn(0).when(originPlanet).getX();
		doReturn(0).when(originPlanet).getY();
		doReturn(15).when(distantPlanet).getX();
		doReturn(15).when(distantPlanet).getY();

		ShipFormation sf = new ShipFormation(1, originPlanet, distantPlanet, JOHN);
		assertThat(sf.getPositionX(), is(0.0));
		assertThat(sf.getPositionY(), is(0.0));

		sf.travel();
		assertThat(sf.getPositionX(), is(greaterThan(0.0)));
		assertThat(sf.getPositionY(), is(greaterThan(0.0)));
		assertThat(sf.getPositionX(), is(lessThan(15.0)));
		assertThat(sf.getPositionY(), is(lessThan(15.0)));
	}

}
