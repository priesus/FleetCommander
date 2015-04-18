package de.spries.fleetcommander.service.core;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import de.spries.fleetcommander.service.core.dto.GameAccessParams;
import de.spries.fleetcommander.service.core.dto.GameParams;

public class GamesServiceIT {

	private GamesService service;
	private int gameId;

	@Before
	public void setUp() {
		service = new GamesService();
		GameAccessParams accessParams = service.createNewGame("Player 1");
		gameId = accessParams.getGameId();
	}

	@Test
	public void canGetCreatedGame() {
		assertThat(service.getGame(gameId), is(notNullValue()));
	}

	@Test
	public void deletesGame() throws Exception {
		service.deleteGame(gameId);
		try {
			service.getGame(gameId);
			fail("Expected exception");
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage(), containsString("doesn't exist"));
		}
	}

	@Test
	public void canAddComputerPlayer() throws Exception {
		assertThat(service.getGame(gameId).getOtherPlayers(), hasSize(0));

		service.addComputerPlayer(gameId);
		assertThat(service.getGame(gameId).getOtherPlayers(), hasSize(1));
		assertThat(service.getGame(gameId).getOtherPlayers().get(0).getName(), is("Computer 1"));

		service.addComputerPlayer(gameId);
		assertThat(service.getGame(gameId).getOtherPlayers(), hasSize(2));
		assertThat(service.getGame(gameId).getOtherPlayers().get(1).getName(), is("Computer 2"));
	}

	@Test
	public void canStartGame() throws Exception {
		service.addComputerPlayer(gameId);
		assertThat(service.getGame(gameId).isStarted(), is(false));

		GameParams params = new GameParams();
		service.modifyGame(gameId, params);
		assertThat(service.getGame(gameId).isStarted(), is(false));

		params.setIsStarted(false);
		service.modifyGame(gameId, params);
		assertThat(service.getGame(gameId).isStarted(), is(false));

		params.setIsStarted(true);
		service.modifyGame(gameId, params);
		assertThat(service.getGame(gameId).isStarted(), is(true));
	}
}
