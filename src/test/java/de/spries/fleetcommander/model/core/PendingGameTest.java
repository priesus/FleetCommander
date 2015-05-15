package de.spries.fleetcommander.model.core;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import de.spries.fleetcommander.model.ai.ComputerPlayer;
import de.spries.fleetcommander.model.core.Game.GameStatus;
import de.spries.fleetcommander.model.core.common.IllegalActionException;

public class PendingGameTest {

	private Game game;
	private Game gameWithPlayers;
	private Player jack;
	private Player john;
	private Player computer;
	private Player otherPlayer;

	@Before
	public void setUp() throws Exception {
		john = mock(Player.class);
		jack = mock(Player.class);
		computer = mock(ComputerPlayer.class);
		otherPlayer = mock(Player.class);

		doReturn(true).when(john).isHumanPlayer();
		doReturn(true).when(jack).isHumanPlayer();
		doReturn(false).when(computer).isHumanPlayer();

		game = new Game();
		game.addPlayer(john);
		gameWithPlayers = new Game();
		gameWithPlayers.addPlayer(jack);
		gameWithPlayers.addPlayer(john);
		gameWithPlayers.addPlayer(computer);
	}

	@Test
	public void initialStatusIsPending() throws Exception {
		assertThat(game.getStatus(), is(GameStatus.PENDING));
	}

	@Test
	public void initialTurnNumberIsZero() throws Exception {
		assertThat(game.getTurnNumber(), is(0));
	}

	@Test(expected = IllegalActionException.class)
	public void gameRequiresAtLeastTwoPlayersToStart() throws Exception {
		game.start(john);
	}

	@Test
	public void playerIsAddedToPlayersList() throws Exception {
		game.addPlayer(jack);
		assertThat(game.getPlayers(), hasItem(jack));
	}

	@Test
	public void gameHasAMaximumOf6Players() throws Exception {
		for (int i = 0; i < 5; i++) {
			game.addPlayer(mock(Player.class));
		}
		try {
			game.addPlayer(mock(Player.class));
			fail("Expected exception");
		} catch (Exception e) {
			assertThat(e.getMessage(), containsString("Limit of 6 players reached"));
		}
	}

	@Test
	public void cannotAddPlayerTwice() throws Exception {
		Game g = new Game();
		g.addPlayer(new Player("John"));
		try {
			g.addPlayer(new Player("John"));
			fail("Excpected exception");
		} catch (IllegalActionException e) {
			//Expected behavior
		}
	}

	@Test(expected = IllegalActionException.class)
	public void cannotEndTurnBeforeGameHasStarted() throws Exception {
		game.endTurn();
	}

	@Test
	public void returnsPlayerWithSameId() throws Exception {
		doReturn(1).when(jack).getId();
		doReturn(12).when(john).getId();
		assertThat(gameWithPlayers.getPlayerWithId(12), is(john));
	}

	@Test
	public void returnsNullForNonexistentPlayerId() throws Exception {
		doReturn(1).when(jack).getId();
		doReturn(12).when(john).getId();
		assertThat(gameWithPlayers.getPlayerWithId(123), is(nullValue()));
	}

	@Test
	public void assignsIdToNewPlayers() throws Exception {
		Game g = new Game();

		Player p1 = mock(Player.class);
		Player p2 = mock(Player.class);
		Player p3 = mock(Player.class);

		g.addPlayer(p1);
		g.addPlayer(p2);
		g.addPlayer(p3);

		verify(p1).setId(1);
		verify(p2).setId(2);
		verify(p3).setId(3);
	}

	@Test
	public void playersAreNotifiedOfGameStart() throws Exception {
		doReturn(true).when(john).isActive();
		doReturn(true).when(jack).isActive();
		gameWithPlayers.start(john);
		gameWithPlayers.start(jack);

		verify(john).notifyNewTurn(gameWithPlayers);
		verify(jack).notifyNewTurn(gameWithPlayers);
	}

	@Test
	public void quittingPlayerIsRemovedFromPendingGame() throws Exception {
		gameWithPlayers.quit(john);
		assertThat(gameWithPlayers.getPlayers(), not(hasItem(john)));
	}

	@Test(expected = IllegalActionException.class)
	public void nonParticipatingPlayerCannotQuitGame() throws Exception {
		gameWithPlayers.quit(otherPlayer);
	}

	@Test
	public void gameHasNoUniverse() throws Exception {
		assertThat(game.getUniverse(), is(nullValue()));
	}

	@Test(expected = IllegalActionException.class)
	public void playerThatDoesntParticipateCannotStartGame() throws Exception {
		gameWithPlayers.start(otherPlayer);
	}

	@Test
	public void gameDoesntStartBeforeAllPlayersAreReady() throws Exception {
		gameWithPlayers.start(john);
		assertThat(gameWithPlayers.getStatus(), is(GameStatus.PENDING));
	}

	@Test(expected = IllegalActionException.class)
	public void cannotStartTwicePerPlayer() throws Exception {
		gameWithPlayers.start(john);
		gameWithPlayers.start(john);
	}

	@Test
	public void gameStartsAfterAllHumanPlayersAreReady() throws Exception {
		gameWithPlayers.start(john);
		gameWithPlayers.start(jack);

		assertThat(gameWithPlayers.getStatus(), is(GameStatus.RUNNING));
	}

}
