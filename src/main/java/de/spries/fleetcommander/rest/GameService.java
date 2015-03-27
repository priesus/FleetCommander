package de.spries.fleetcommander.rest;

import java.util.Arrays;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
	public Response createGame() {
		Game game = createSinglePlayerGame();
		int gameId = GameStore.INSTANCE.create(game);
		game.setId(gameId);
		String token = GameAuthenticator.INSTANCE.createAuthToken(gameId);

		return getNoCacheResponseBuilder(Response.Status.CREATED).header("Location", "/rest/games/" + gameId)
				.entity("{\"gameAuthToken\": \"" + token + "\"}").build();
	}

	@GET
	@Path("games/{id:\\d+}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getGame(@PathParam("id") int id, @Context HttpHeaders httpHeaders) {
		String token = httpHeaders.getHeaderString("Authorization");
		if (!GameAuthenticator.INSTANCE.isAuthTokenValid(id, token)) {
			return getNoCacheResponseBuilder(Response.Status.UNAUTHORIZED).build();
		}

		Game entity = GameStore.INSTANCE.get(id);
		if (entity == null) {
			return getNoCacheResponseBuilder(Response.Status.NOT_FOUND).build();
		}

		return getNoCacheResponseBuilder(Response.Status.OK).entity(entity).build();
	}

	@DELETE
	@Path("games/{id:\\d+}")
	public Response quitGame(@PathParam("id") int id, @Context HttpHeaders httpHeaders) {
		String token = httpHeaders.getHeaderString("Authorization");
		if (!GameAuthenticator.INSTANCE.isAuthTokenValid(id, token)) {
			return getNoCacheResponseBuilder(Response.Status.UNAUTHORIZED).build();
		}
		GameStore.INSTANCE.delete(id);
		return getNoCacheResponseBuilder(Response.Status.OK).build();
	}

	@POST
	@Path("games/{id:\\d+}/turns")
	public Response endTurn(@PathParam("id") int gameId, @Context HttpHeaders httpHeaders) {
		String token = httpHeaders.getHeaderString("Authorization");
		if (!GameAuthenticator.INSTANCE.isAuthTokenValid(gameId, token)) {
			return getNoCacheResponseBuilder(Response.Status.UNAUTHORIZED).build();
		}

		Game game = GameStore.INSTANCE.get(gameId);
		game.endTurn();

		return getNoCacheResponseBuilder(Response.Status.OK).build();
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

	private Response.ResponseBuilder getNoCacheResponseBuilder(Response.Status status) {
		CacheControl cc = new CacheControl();
		cc.setNoCache(true);
		cc.setMaxAge(-1);
		cc.setMustRevalidate(true);

		return Response.status(status).cacheControl(cc);
	}

	private Game createSinglePlayerGame() {
		Game game = new Game();
		Player p = game.createHumanPlayer("Player 1");
		game.setUniverse(UniverseGenerator.generate(Arrays.asList(p)));
		return game;
	}
}
