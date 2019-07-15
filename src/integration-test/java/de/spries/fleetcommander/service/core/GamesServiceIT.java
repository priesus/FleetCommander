package de.spries.fleetcommander.service.core;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import de.spries.fleetcommander.model.core.Game.Status;
import de.spries.fleetcommander.model.core.common.IllegalActionException;
import de.spries.fleetcommander.service.core.dto.GameAccessParams;
import de.spries.fleetcommander.service.core.dto.GameParams;
import de.spries.fleetcommander.service.core.dto.GamePlayer;

public class GamesServiceIT {

	private GamesService service;
	private GamePlayer gamePlayer;

	@Before
	public void setUp() {
		service = new GamesService();
		GameAccessParams accessParams = service.createNewGame("Player 1");
		gamePlayer = GamePlayer.Companion.forIds(accessParams.getGameId(), accessParams.getPlayerId());
	}

	@Test
	public void canGetCreatedGame() {
		assertThat(service.getGame(gamePlayer), is(notNullValue()));
	}

	@Test
	public void canAddComputerPlayer() throws Exception {
		assertThat(service.getGame(gamePlayer).getOtherPlayers(), hasSize(0));

		service.addComputerPlayer(gamePlayer);
		assertThat(service.getGame(gamePlayer).getOtherPlayers(), hasSize(1));
		assertThat(service.getGame(gamePlayer).getOtherPlayers().get(0).getName(), is("Computer 1"));

		service.addComputerPlayer(gamePlayer);
		assertThat(service.getGame(gamePlayer).getOtherPlayers(), hasSize(2));
		assertThat(service.getGame(gamePlayer).getOtherPlayers().get(1).getName(), is("Computer 2"));
	}

	@Test
	public void canStartGame() throws Exception {
		service.addComputerPlayer(gamePlayer);
		assertThat(service.getGame(gamePlayer).getStatus(), is(Status.PENDING));

		service.modifyGame(gamePlayer, new GameParams(false));
		assertThat(service.getGame(gamePlayer).getStatus(), is(Status.PENDING));

		service.modifyGame(gamePlayer, new GameParams(false));
		assertThat(service.getGame(gamePlayer).getStatus(), is(Status.PENDING));

		service.modifyGame(gamePlayer, new GameParams(true));
		assertThat(service.getGame(gamePlayer).getStatus(), is(Status.RUNNING));
	}

	@Test
	public void canJoinViaCreatedJoinCode() throws Exception {
		int gameId = gamePlayer.getGameId();

		String joinCode = service.createJoinCode(gameId);
		assertThat(service.getActiveJoinCodes(gameId), contains(joinCode));

		GameAccessParams player2AccessParams = service.joinGame("Player 2", joinCode);
		assertThat(player2AccessParams.getGameId(), is(gameId));
		assertThat(service.getGame(gamePlayer).getOtherPlayers(), hasSize(1));
		assertThat(service.getGame(player2AccessParams.getGamePlayer()).getOtherPlayers(), hasSize(1));
	}

	@Test(expected = IllegalActionException.class)
	public void playerCannotAccessPendingGameAfterQuitting() throws Exception {
		service.quitGame(gamePlayer);
		service.getGame(gamePlayer);
	}

	@Test
	public void startingGameInvalidatesAllJoinCodes() throws Exception {
		service.addComputerPlayer(gamePlayer);
		int gameId = gamePlayer.getGameId();

		service.createJoinCode(gameId);
		service.createJoinCode(gameId);
		service.createJoinCode(gameId);

		GameParams params = new GameParams(true);
		service.modifyGame(gamePlayer, params);

		assertThat(service.getActiveJoinCodes(gameId), is(empty()));
	}
}
