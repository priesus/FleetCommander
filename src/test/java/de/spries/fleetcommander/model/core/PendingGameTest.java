package de.spries.fleetcommander.model.core;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import de.spries.fleetcommander.model.core.Game.GameStatus;
import de.spries.fleetcommander.model.core.common.IllegalActionException;

public class PendingGameTest {

	private Game game;
	private Player jack;
	private Player john;
	private Player otherPlayer;

	@Before
	public void setUp() throws Exception {
		john = mock(Player.class);
		jack = mock(Player.class);
		otherPlayer = mock(Player.class);

		game = new Game();
		game.addPlayer(john);
	}

	@Test
	public void initialStatusIsPending() throws Exception {
		assertThat(game.getStatus(), is(GameStatus.PENDING));
	}

	@Test(expected = IllegalActionException.class)
	public void gameRequiresAtLeastTwoPlayersToStart() throws Exception {
		game.start();
	}

	@Test
	public void playerIsAddedToPlayersList() throws Exception {
		game.addPlayer(john);
		assertThat(game.getPlayers(), hasItem(john));
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

	@Test(expected = IllegalActionException.class)
	public void cannotEndTurnBeforeGameHasStarted() throws Exception {
		game.endTurn();
	}

	@Test
	public void returnsPlayerWithSameId() throws Exception {
		game.addPlayer(otherPlayer);
		doReturn(12).when(john).getId();
		doReturn(123).when(otherPlayer).getId();
		assertThat(game.getPlayerWithId(123), is(otherPlayer));
	}

	@Test
	public void returnsNullForNonexistentPlayerId() throws Exception {
		assertThat(game.getPlayerWithId(123), is(nullValue()));
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
		game.addPlayer(jack);
		game.start();

		verify(john).notifyNewTurn(game);
		verify(jack).notifyNewTurn(game);
	}

	@Test
	public void quittingPlayerIsRemovedFromPendingGame() throws Exception {
		game.quit(john);
		assertThat(game.getPlayers(), is(empty()));
	}

	@Test(expected = IllegalActionException.class)
	public void nonParticipatingPlayerCannotQuitGame() throws Exception {
		game.quit(otherPlayer);
	}

	@Test
	public void gameHasNoUniverse() throws Exception {
		assertThat(game.getUniverse(), is(nullValue()));
	}

}
