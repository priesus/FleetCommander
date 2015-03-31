package de.spries.fleetcommander.model;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import de.spries.fleetcommander.model.Game.NotEnoughPlayersException;
import de.spries.fleetcommander.model.player.Player;
import de.spries.fleetcommander.model.universe.Universe;

public class GameTest {

	private Game game;
	private Game startedGame;
	private Universe universe;
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

		universe = mock(Universe.class);
		startedGame = new Game();
		startedGame.addPlayer(john);
		startedGame.addPlayer(jack);
		startedGame.setUniverse(universe);
		startedGame.start();
	}

	@Test(expected = NotEnoughPlayersException.class)
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

	@Test(expected = IllegalStateException.class)
	public void cannotAddPlayersAfterGameHasStarted() throws Exception {
		startedGame.addPlayer(otherPlayer);
	}

	@Test(expected = IllegalStateException.class)
	public void cannotEndTurnBeforeGameHasStarted() throws Exception {
		game.endTurn();
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
		startedGame.endTurn(john);

		verify(universe, never()).runFactoryProductionCycle();
		verify(universe, never()).runShipTravellingCycle();
	}

	@Test(expected = IllegalArgumentException.class)
	public void cannotEndPlayerTurnTwiceBeforeGameTurnEnded() throws Exception {
		startedGame.endTurn(john);
		startedGame.endTurn(john);
	}

	@Test
	public void turnEndsAfterAllPlayersHaveEndedTheirTurn() throws Exception {
		startedGame.endTurn(john);
		startedGame.endTurn(jack);

		verify(universe).runFactoryProductionCycle();
		verify(universe).runShipTravellingCycle();
	}

	@Test
	public void turnEndsOnlyOnceAfterAllPlayersHaveEndedTheirTurn() throws Exception {
		startedGame.endTurn(john);
		startedGame.endTurn(jack);

		startedGame.endTurn(john);

		verify(universe).runFactoryProductionCycle();
		verify(universe).runShipTravellingCycle();
	}

}
