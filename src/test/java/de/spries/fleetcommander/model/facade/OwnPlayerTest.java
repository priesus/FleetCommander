package de.spries.fleetcommander.model.facade;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import de.spries.fleetcommander.model.core.Player;

public class OwnPlayerTest {

	private Player originalPlayer;
	private OwnPlayer viewingPlayer;

	@Before
	public void setUp() {
		originalPlayer = mock(Player.class);
		viewingPlayer = new OwnPlayer(originalPlayer);
	}

	@Test
	public void forwardsCallToGetNameForOtherPlayers() {
		viewingPlayer.getCredits();
		verify(originalPlayer).getCredits();
	}
}
