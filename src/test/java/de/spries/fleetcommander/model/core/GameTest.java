package de.spries.fleetcommander.model.core;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import de.spries.fleetcommander.model.core.Game.GameStatus;
import de.spries.fleetcommander.model.core.universe.Planet;
import de.spries.fleetcommander.model.core.universe.Universe;

public class GameTest {

	private Game game;
	private Game startedGame;
	private Universe universe;
	private Player jack;
	private Player john;
	private Player computerPlayer;
	private Player computerPlayer2;
	private Player otherPlayer;
	private Planet someHomePlanet;

	@Before
	public void setUp() throws Exception {
		john = mock(Player.class);
		jack = mock(Player.class);
		computerPlayer = mock(Player.class);
		computerPlayer2 = mock(Player.class);
		otherPlayer = mock(Player.class);

		doReturn(true).when(john).isHumanPlayer();
		doReturn(true).when(jack).isHumanPlayer();
		doReturn(false).when(computerPlayer).isHumanPlayer();
		doReturn(false).when(computerPlayer2).isHumanPlayer();

		game = new Game();
		game.addPlayer(john);

		universe = mock(Universe.class);
		startedGame = new Game();
		startedGame.addPlayer(john);
		startedGame.addPlayer(jack);
		startedGame.addPlayer(computerPlayer);
		startedGame.addPlayer(computerPlayer2);
		startedGame.setUniverse(universe);
		startedGame.start();

		someHomePlanet = mock(Planet.class);
	}

	@Test
	public void initialStatusIsPending() throws Exception {
		assertThat(game.getStatus(), is(GameStatus.PENDING));
	}

	@Test
	public void statusIsRunningAfterGameStarted() throws Exception {
		assertThat(startedGame.getStatus(), is(GameStatus.RUNNING));
	}

	@Test(expected = IllegalStateException.class)
	public void gameRequiresAtLeastTwoPlayersToStart() throws Exception {
		game.start();
	}

	@Test(expected = IllegalStateException.class)
	public void gameRequiresAUniverseToStart() throws Exception {
		game.addPlayer(jack);
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

	@Test(expected = IllegalStateException.class)
	public void cannotAddPlayersAfterGameHasStarted() throws Exception {
		startedGame.addPlayer(otherPlayer);
	}

	@Test(expected = IllegalStateException.class)
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
	public void endingTurnRunsFactoryCycle() throws Exception {
		startedGame.endTurn();
		verify(universe).runFactoryProductionCycle();
	}

	@Test
	public void endingTurnRunsShipTravellingCycle() throws Exception {
		startedGame.endTurn();
		verify(universe).runShipTravellingCycle();
	}

	@Test(expected = IllegalArgumentException.class)
	public void playerThatDoesntParticipateCannotEndTurn() throws Exception {
		startedGame.endTurn(otherPlayer);
	}

	@Test
	public void turnDoesntEndBeforeAllPlayersHaveEndedTheirTurn() throws Exception {
		doReturn(true).when(john).isActive();
		doReturn(true).when(jack).isActive();
		startedGame.endTurn(john);

		verify(universe, never()).runFactoryProductionCycle();
		verify(universe, never()).runShipTravellingCycle();
	}

	@Test(expected = IllegalArgumentException.class)
	public void cannotEndPlayerTurnTwiceBeforeGameTurnEnded() throws Exception {
		doReturn(true).when(john).isActive();
		doReturn(true).when(jack).isActive();
		startedGame.endTurn(john);
		startedGame.endTurn(john);
	}

	@Test(expected = IllegalArgumentException.class)
	public void defeatedPlayersCannotEndTurn() throws Exception {
		doReturn(false).when(john).isActive();
		startedGame.endTurn(john);
	}

	@Test
	public void turnEndsAfterAllPlayersHaveEndedTheirTurn() throws Exception {
		doReturn(true).when(john).isActive();
		doReturn(true).when(jack).isActive();
		startedGame.endTurn(john);
		startedGame.endTurn(jack);

		verify(universe).runFactoryProductionCycle();
		verify(universe).runShipTravellingCycle();
	}

	@Test
	public void turnEndsAfterAllActivePlayersHaveEndedTheirTurn() throws Exception {
		doReturn(true).when(john).isActive();
		doReturn(false).when(jack).isActive();

		startedGame.endTurn(john);

		verify(universe).runFactoryProductionCycle();
		verify(universe).runShipTravellingCycle();
	}

	@Test
	public void turnEndsOnlyOnceAfterAllPlayersHaveEndedTheirTurn() throws Exception {
		doReturn(true).when(john).isActive();
		doReturn(true).when(jack).isActive();
		startedGame.endTurn(john);
		startedGame.endTurn(jack);

		startedGame.endTurn(john);

		verify(universe).runFactoryProductionCycle();
		verify(universe).runShipTravellingCycle();
	}

	@Test
	public void activePlayersAreNotifiedOfTurnEnd() throws Exception {
		doReturn(true).when(john).isActive();
		doReturn(true).when(jack).isActive();
		doReturn(true).when(computerPlayer).isActive();
		doReturn(true).when(computerPlayer2).isActive();
		startedGame.endTurn();

		verify(john).notifyNewTurn(startedGame);
		verify(jack).notifyNewTurn(startedGame);
		verify(computerPlayer).notifyNewTurn(startedGame);
		verify(computerPlayer2).notifyNewTurn(startedGame);
	}

	@Test
	public void defeatedPlayersAreNotNotifiedOfTurnEnd() throws Exception {
		doReturn(true).when(john).isActive();
		doReturn(false).when(jack).isActive();
		doReturn(false).when(computerPlayer).isActive();
		doReturn(true).when(computerPlayer2).isActive();
		startedGame.endTurn();

		verify(john).notifyNewTurn(startedGame);
		verify(jack, never()).notifyNewTurn(startedGame);
		verify(computerPlayer, never()).notifyNewTurn(startedGame);
		verify(computerPlayer2).notifyNewTurn(startedGame);
	}

	@Test
	public void setsNewDefeatedPlayersInactive() throws Exception {
		doReturn(true).when(john).isActive();
		doReturn(false).when(jack).isActive();
		doReturn(null).when(universe).getHomePlanetOf(john);
		doReturn(null).when(universe).getHomePlanetOf(jack);
		startedGame.endTurn();

		verify(john).setActive(false);
		verify(jack, never()).setActive(Mockito.anyBoolean());
	}

	@Test
	public void notifiesUniverseForDefeatedPlayers() throws Exception {
		doReturn(true).when(john).isActive();
		doReturn(true).when(jack).isActive();
		doReturn(someHomePlanet).when(universe).getHomePlanetOf(john);
		doReturn(null).when(universe).getHomePlanetOf(jack);
		startedGame.endTurn();

		verify(universe).handleDefeatedPlayer(jack);
		verify(universe, never()).handleDefeatedPlayer(john);
	}

	@Test
	public void gameOverWhenLastPlayerIsHuman() throws Exception {
		doReturn(true).when(john).isActive();
		doReturn(false).when(jack).isActive();
		doReturn(false).when(computerPlayer).isActive();
		doReturn(false).when(computerPlayer2).isActive();
		startedGame.endTurn();

		assertThat(startedGame.getStatus(), is(GameStatus.OVER));
	}

	@Test
	public void gameOverWhen0HumanPlayersLeft() throws Exception {
		doReturn(false).when(john).isActive();
		doReturn(false).when(jack).isActive();
		doReturn(true).when(computerPlayer).isActive();
		doReturn(true).when(computerPlayer2).isActive();
		startedGame.endTurn();

		assertThat(startedGame.getStatus(), is(GameStatus.OVER));
	}

	@Test
	public void noPlayersNotifiedAfterGameEnd() throws Exception {
		doReturn(true).when(john).isActive();
		doReturn(false).when(jack).isActive();
		doReturn(false).when(computerPlayer).isActive();
		doReturn(false).when(computerPlayer2).isActive();
		startedGame.endTurn();

		verify(john, never()).notifyNewTurn(startedGame);
		verify(jack, never()).notifyNewTurn(startedGame);
		verify(computerPlayer, never()).notifyNewTurn(startedGame);
	}

	@Test
	public void playersAreNotifiedOfGameStart() throws Exception {
		doReturn(true).when(john).isActive();
		doReturn(true).when(jack).isActive();
		game.addPlayer(jack);
		game.setUniverse(universe);
		game.start();

		verify(john).notifyNewTurn(game);
		verify(jack).notifyNewTurn(game);
	}

}
