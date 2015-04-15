package de.spries.fleetcommander.model.facade;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import de.spries.fleetcommander.model.core.Player;
import de.spries.fleetcommander.model.core.universe.Planet;

public class PlayerSpecificPlanetTest {

	private Planet originalPlanet;
	private Player self;
	private Player otherPlayer;
	private PlayerSpecificPlanet ownPlanet;
	private PlayerSpecificPlanet otherPlayersPlanet;

	@Before
	public void setUp() {
		originalPlanet = mock(Planet.class);
		self = mock(Player.class);
		otherPlayer = mock(Player.class);

		ownPlanet = new PlayerSpecificPlanet(originalPlanet, self);
		otherPlayersPlanet = new PlayerSpecificPlanet(originalPlanet, otherPlayer);

		doReturn(true).when(originalPlanet).isInhabitedBy(self);
		doReturn(false).when(originalPlanet).isInhabitedBy(otherPlayer);
	}

	@Test
	public void forwardsCallToGetId() {
		otherPlayersPlanet.getId();
		verify(originalPlanet).getId();
	}

	@Test
	public void forwardsCallToGetX() {
		otherPlayersPlanet.getX();
		verify(originalPlanet).getX();
	}

	@Test
	public void forwardsCallToGetY() {
		otherPlayersPlanet.getY();
		verify(originalPlanet).getY();
	}

	@Test
	public void forwardsCallToIsHomePlanet() {
		ownPlanet.isMyHomePlanet();
		verify(originalPlanet).isHomePlanetOf(self);
	}

	@Test
	public void forwardsCallToIsInhabitedBy() {
		ownPlanet.isInhabitedByMe();
		verify(originalPlanet).isInhabitedBy(self);
	}

	@Test
	public void forwardsCallToGetShipCountForSelf() {
		doReturn(true).when(originalPlanet).isInhabitedBy(self);

		ownPlanet.getShipCount();
		verify(originalPlanet).getShipCount();
	}

	@Test
	public void doesNotReturnShipCountForOtherPlayers() {
		assertThat(otherPlayersPlanet.getShipCount(), is(0));
		verify(originalPlanet, never()).getShipCount();
	}

	@Test
	public void forwardsCallToGetIncomingShips() {
		ownPlanet.getIncomingShipCount();
		verify(originalPlanet).getIncomingShipCount(self);
	}

	@Test
	public void forwardsCallToGetFactorySiteForSelf() {
		ownPlanet.getFactorySite();
		verify(originalPlanet).getFactorySite();
	}

	@Test
	public void doesNotReturnFactorySiteForOtherPlayers() {
		assertThat(otherPlayersPlanet.getFactorySite(), is(nullValue()));
		verify(originalPlanet, never()).getFactorySite();
	}

	@Test
	public void forwardsCallToBuildFactory() {
		ownPlanet.buildFactory();
		verify(originalPlanet).buildFactory(self);
	}

}
