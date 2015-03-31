package de.spries.fleetcommander.model.player;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import de.spries.fleetcommander.model.Game;

public class ComputerPlayerTest {

	private Player player;
	private Game game;

	@Before
	public void setUp() {
		game = mock(Game.class);
		player = new ComputerPlayer("Computer");
	}

	@Test
	public void endsTurnInNewTurn() {
		player.notifyNewTurn(game);
		verify(game).endTurn(player);
	}

}
