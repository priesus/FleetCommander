package de.spries.fleetcommander.service.core;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import de.spries.fleetcommander.service.core.dto.GameAccessParams;

public class GamesServiceIT {

	private GamesService service;

	@Before
	public void setUp() {
		service = new GamesService();
	}

	@Test
	public void canGetCreatedGame() {
		GameAccessParams accessParams = service.createNewGame("Player 1");
		int gameId = accessParams.getGameId();
		assertThat(service.getGame(gameId), is(notNullValue()));
	}
}
