package de.spries.fleetcommander.facade;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import de.spries.fleetcommander.model.Player;

public class PlayerSpecificPlayerTest {

	private Player self;
	private Player otherViewingPlayer;
	private PlayerSpecificPlayer ownPlayerView;
	private PlayerSpecificPlayer otherPlayerView;

	@Before
	public void setUp() {
		self = mock(Player.class);
		otherViewingPlayer = mock(Player.class);
		ownPlayerView = new PlayerSpecificPlayer(self, self);
		otherPlayerView = new PlayerSpecificPlayer(self, otherViewingPlayer);
	}

	@Test
	public void forwardsCallToGetNameForSelf() {
		ownPlayerView.getName();
		verify(self).getName();
	}

	@Test
	public void forwardsCallToGetNameForOtherPlayers() {
		otherPlayerView.getName();
		verify(self).getName();
	}

	@Test
	public void forwardsCallToGetCreditsForSelf() {
		ownPlayerView.getCredits();
		verify(self).getCredits();
	}

	@Test
	public void doesNotReturnCreditsForOtherPlayers() {
		assertThat(otherPlayerView.getCredits(), is(nullValue()));
		verify(self, never()).getCredits();
	}
}
