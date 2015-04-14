package de.spries.fleetcommander.model.facade;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import de.spries.fleetcommander.model.core.Player;
import de.spries.fleetcommander.model.core.universe.Planet;
import de.spries.fleetcommander.model.core.universe.ShipFormation;
import de.spries.fleetcommander.model.core.universe.Universe;
import de.spries.fleetcommander.model.facade.PlayerSpecificPlanet;
import de.spries.fleetcommander.model.facade.PlayerSpecificUniverse;

public class PlayerSpecificUniverseTest {

	private Universe originalUniverse;
	private Player self;
	private Player otherPlayer;
	private PlayerSpecificUniverse ownUniverseView;
	private PlayerSpecificUniverse otherUniverseView;
	private Planet myHomePlanet;
	private Planet otherPlayersHomePlanet;
	private ShipFormation myShips;
	private ShipFormation otherPlayersShips;

	@Before
	public void setUp() {
		originalUniverse = mock(Universe.class);
		self = mock(Player.class);
		otherPlayer = mock(Player.class);

		myHomePlanet = mock(Planet.class);
		otherPlayersHomePlanet = mock(Planet.class);

		doReturn(Arrays.asList(mock(Planet.class))).when(originalUniverse).getPlanets();
		doReturn(myHomePlanet).when(originalUniverse).getHomePlanetOf(self);
		doReturn(otherPlayersHomePlanet).when(originalUniverse).getHomePlanetOf(otherPlayer);

		myShips = mock(ShipFormation.class);
		otherPlayersShips = mock(ShipFormation.class);
		doReturn(self).when(myShips).getCommander();
		doReturn(otherPlayer).when(otherPlayersShips).getCommander();
		doReturn(Arrays.asList(myShips, otherPlayersShips)).when(originalUniverse).getTravellingShipFormations();

		ownUniverseView = new PlayerSpecificUniverse(originalUniverse, self);
		otherUniverseView = new PlayerSpecificUniverse(originalUniverse, otherPlayer);
	}

	@Test
	public void returnsPlayerSpecificPlanets() {
		assertThat(ownUniverseView.getPlanets(), hasSize(1));
	}

	@Test
	public void returnsPlayerSpecificHomePlanet() {
		assertThat(ownUniverseView.getHomePlanet(), is(new PlayerSpecificPlanet(myHomePlanet, self)));
		assertThat(otherUniverseView.getHomePlanet(), is(new PlayerSpecificPlanet(otherPlayersHomePlanet, otherPlayer)));
	}

	@Test
	public void forwardsCallToSendShipsForSelf() {
		ownUniverseView.sendShips(1, 2, 3);
		verify(originalUniverse).sendShips(1, 2, 3, self);
	}

	@Test
	public void forwardsCallToSendShipsForOtherPlayers() {
		otherUniverseView.sendShips(1, 2, 3);
		verify(originalUniverse).sendShips(1, 2, 3, otherPlayer);
	}

	@Test
	public void returnsOnlyOwnTravellingShips() throws Exception {
		assertThat(ownUniverseView.getTravellingShipFormations(), hasSize(1));
		assertThat(ownUniverseView.getTravellingShipFormations(), hasItem(myShips));

		assertThat(otherUniverseView.getTravellingShipFormations(), hasSize(1));
		assertThat(otherUniverseView.getTravellingShipFormations(), hasItem(otherPlayersShips));
	}

}
