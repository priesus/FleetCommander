package de.spries.fleetcommander.rest;

import java.util.Arrays;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.spries.fleetcommander.model.Game;
import de.spries.fleetcommander.model.player.Player;
import de.spries.fleetcommander.model.universe.NotEnoughShipsException;
import de.spries.fleetcommander.model.universe.Planet.NotPlayersOwnPlanetException;
import de.spries.fleetcommander.model.universe.UniverseGenerator;
import de.spries.fleetcommander.persistence.GameStore;

@Path("")
public class GameService {

	@POST
	@Path("games")
	@Produces(MediaType.APPLICATION_JSON)
	public Game startGame() {
		Game game = new Game();
		int gameId = GameStore.INSTANCE.create(game);
		game.setId(gameId);
		Player p = game.createHumanPlayer("Player 1");
		game.setUniverse(UniverseGenerator.generate(Arrays.asList(p)));
		return game;
	}

	@GET
	@Path("games/{id:\\d+}")
	@Produces(MediaType.APPLICATION_JSON)
	public Game getGame(@PathParam("id") int id) {
		return GameStore.INSTANCE.get(id);
	}

	@DELETE
	@Path("games/{id:\\d+}")
	public void quitGame(@PathParam("id") int id) {
		GameStore.INSTANCE.delete(id);
	}

	@POST
	@Path("games/{id:\\d+}/turns")
	public void endTurn(@PathParam("id") int gameId) {

		Game game = GameStore.INSTANCE.get(gameId);
		if (game != null) {
			// TODO identify player
			game.endTurn();
		}
		// TODO Error handling: game doesn't exist
		// TODO error handling: game is not the player's own game
	}

	// TODO accept parameters as JSON
	@POST
	@Path("games/{id:\\d+}/universe/travellingShipFormations/{ships:\\d+}/{origin:\\d+}/{dest:\\d+}")
	public void sendShips(@PathParam("id") int gameId, @PathParam("ships") int shipCount,
			@PathParam("origin") int originPlanetId, @PathParam("dest") int destinationPlanetId) {

		Game game = GameStore.INSTANCE.get(gameId);
		if (game != null) {
			// TODO identify player
			Player player = game.getPlayers().get(0);
			try {
				game.getUniverse().sendShips(shipCount, originPlanetId, destinationPlanetId, player);
			} catch (NotPlayersOwnPlanetException | NotEnoughShipsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// TODO Error handling: game doesn't exist
		// TODO error handling: game is not the player's own game
	}
}
