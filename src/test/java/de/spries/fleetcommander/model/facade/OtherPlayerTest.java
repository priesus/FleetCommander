package de.spries.fleetcommander.model.facade;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import de.spries.fleetcommander.model.core.Player;

public class OtherPlayerTest {

	private Player originalPlayer;
	private OtherPlayer viewingPlayer;

	@Before
	public void setUp() {
		originalPlayer = mock(Player.class);
		viewingPlayer = new OtherPlayer(originalPlayer);
	}

	@Test
	public void forwardsCallToGetNameForOtherPlayers() {
		viewingPlayer.getName();
		verify(originalPlayer).getName();
	}

	@Test
	public void forwardsCallToIsActive() {
		viewingPlayer.isActive();
		verify(originalPlayer).isActive();
	}
}
