package de.spries.fleetcommander.rest;

import java.util.Arrays;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.spries.fleetcommander.model.Game;
import de.spries.fleetcommander.model.player.Player;
import de.spries.fleetcommander.model.universe.UniverseGenerator;

@Path("")
public class GameService {

	@POST
	@Path("games")
	@Produces(MediaType.APPLICATION_JSON)
	public Game startGame() {
		Game game = new Game();
		Player p = game.createHumanPlayer("Player 1");
		game.setUniverse(UniverseGenerator.generate(Arrays.asList(p)));
		return game;
	}

	@GET
	@Path("games/{id:\\d+}")
	@Produces(MediaType.APPLICATION_JSON)
	public Game getGame() {
		// TODO implement
		return startGame();
	}

	@DELETE
	@Path("games/{id:\\d+}")
	public void quitGame() {
		// TODO implement
	}
}
