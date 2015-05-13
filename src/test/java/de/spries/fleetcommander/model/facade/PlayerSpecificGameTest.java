package de.spries.fleetcommander.model.facade;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import de.spries.fleetcommander.model.core.Game;
import de.spries.fleetcommander.model.core.Player;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.management.*")
@PrepareForTest({ PlayerSpecificUniverse.class, PlayerSpecificTurnEvents.class })
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
		PowerMockito.mockStatic(PlayerSpecificTurnEvents.class);

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
	public void addsHumanPlayerWithName() throws Exception {
		ownGame.addHumanPlayer("Player 2");

		ArgumentCaptor<Player> argument = ArgumentCaptor.forClass(Player.class);
		verify(originalGame).addPlayer(argument.capture());
		assertThat(argument.getValue().getName(), is("Player 2"));
	}

	@Test
	public void forwardsCallToStart() {
		ownGame.start();
		verify(originalGame).start();
	}

	@Test
	public void forwardsCallToGetStatus() {
		ownGame.getStatus();
		verify(originalGame).getStatus();
	}

	@Test
	public void forwardsCallToGetPreviousTurnEvents() throws Exception {
		ownGame.getPreviousTurnEvents();
		verify(originalGame).getPreviousTurnEvents();
	}

	@Test
	public void returnsNullTurnEventsIfOriginalGameHasNoEventsYet() throws Exception {
		doReturn(null).when(originalGame).getPreviousTurnEvents();
		assertThat(ownGame.getPreviousTurnEvents(), is(nullValue()));
	}

	@Test
	public void forwardsCallToEndTurn() {
		ownGame.endTurn();
		verify(originalGame).endTurn(self);
	}

	@Test
	public void forwardsCallToQuit() {
		ownGame.quit();
		verify(originalGame).quit(self);
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
