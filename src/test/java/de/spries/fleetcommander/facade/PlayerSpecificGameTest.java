package de.spries.fleetcommander.facade;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import de.spries.fleetcommander.model.Game;
import de.spries.fleetcommander.model.Player;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PlayerSpecificUniverse.class)
public class PlayerSpecificGameTest {

	private Game originalGame;
	private Player self;
	private PlayerSpecificGame ownGame;

	@Before
	public void setUp() {
		originalGame = mock(Game.class);
		self = mock(Player.class);

		PowerMockito.mockStatic(PlayerSpecificUniverse.class);

		doReturn(Arrays.asList(self)).when(originalGame).getPlayers();
		ownGame = new PlayerSpecificGame(originalGame, self);
	}

	@Test
	public void forwardsCallToGetId() {
		ownGame.getId();
		verify(originalGame).getId();
	}

	@Test
	public void forwardsCallToEndTurn() {
		ownGame.endTurn();
		verify(originalGame).endTurn(self);
	}

	@Test
	public void returnsPlayerSpecificPlayers() throws Exception {
		assertThat(ownGame.getPlayers(), hasSize(1));
	}

}
