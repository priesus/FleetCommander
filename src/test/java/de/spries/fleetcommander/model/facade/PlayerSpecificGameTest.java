package de.spries.fleetcommander.model.facade;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import de.spries.fleetcommander.model.core.Game;
import de.spries.fleetcommander.model.core.Player;
import de.spries.fleetcommander.model.core.universe.Universe;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.management.*")
@PrepareForTest(PlayerSpecificUniverse.class)
public class PlayerSpecificGameTest {

	private Game originalGame;
	private Player self;
	private Player otherPlayer;
	private PlayerSpecificGame ownGame;

	@Before
	public void setUp() {
		originalGame = mock(Game.class);
		self = mock(Player.class);
		otherPlayer = mock(Player.class);

		doReturn("Myself").when(self).getName();

		PowerMockito.mockStatic(PlayerSpecificUniverse.class);

		doReturn(Arrays.asList(self, otherPlayer)).when(originalGame).getPlayers();
		ownGame = new PlayerSpecificGame(originalGame, self);
	}

	@Test
	public void forwardsCallToGetId() {
		ownGame.getId();
		verify(originalGame).getId();
	}

	@Test
	public void addsComputerPlayerWithName() throws Exception {
		ownGame.addComputerPlayer();

		ArgumentCaptor<Player> argument = ArgumentCaptor.forClass(Player.class);
		verify(originalGame).addPlayer(argument.capture());
		assertThat(argument.getValue().getName(), is("Computer 2"));
	}

	@Test
	public void forwardsCallToStartAndSetUniverse() {
		ownGame.start();
		verify(originalGame).setUniverse(Mockito.any(Universe.class));
		verify(originalGame).start();
	}

	@Test
	public void forwardsCallToIsStarted() {
		ownGame.isStarted();
		verify(originalGame).isStarted();
	}

	@Test
	public void forwardsCallToEndTurn() {
		ownGame.endTurn();
		verify(originalGame).endTurn(self);
	}

	@Test
	public void returnsOwnPlayer() throws Exception {
		assertThat(ownGame.getMe().getName(), is("Myself"));
	}

	@Test
	public void returnsOtherPlayers() throws Exception {
		assertThat(ownGame.getOtherPlayers(), hasSize(1));
	}

}
