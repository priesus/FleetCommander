package de.spries.fleetcommander.rest;

import java.util.Arrays;

import javax.ws.rs.Consumes;
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

import de.spries.fleetcommander.model.ComputerPlayer;
import de.spries.fleetcommander.model.Game;
import de.spries.fleetcommander.model.Player;
import de.spries.fleetcommander.model.common.IllegalActionException;
import de.spries.fleetcommander.model.universe.UniverseGenerator;
import de.spries.fleetcommander.persistence.GameStore;

@Path("")
public class GameService {

	public static class ShipFormation {
		public int shipCount;
		public int originPlanetId;
		public int destinationPlanetId;
	}

	@POST
	@Path("games")
	public Response createGame() {
		Game game = createSinglePlayerGame();
		int gameId = GameStore.INSTANCE.create(game);
		game.setId(gameId);
		String token = GameAuthenticator.INSTANCE.createAuthToken(gameId);

		return getNoCacheResponseBuilder(Response.Status.CREATED).header("Location", "/rest/games/" + gameId)
				.entity("{\"gameAuthToken\": \"" + token + "\", \"gameId\": \"" + gameId + "\"}").build();
	}

	@GET
	@Path("games/{id:\\d+}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getGame(@PathParam("id") int id) {

		//TODO players should only be able to see limited details of the universe (i.e. no enemy locations)

		Game game = GameStore.INSTANCE.get(id);
		return getNoCacheResponseBuilder(Response.Status.OK).entity(game).build();
	}

	@DELETE
	@Path("games/{id:\\d+}")
	public Response quitGame(@PathParam("id") int id, @Context HttpHeaders httpHeaders) {
		String token = GameAccessTokenFilter.extractAuthTokenFromHeaders(httpHeaders);

		GameAuthenticator.INSTANCE.deleteAuthToken(id, token);
		GameStore.INSTANCE.delete(id);
		return getNoCacheResponseBuilder(Response.Status.OK).build();
	}

	@POST
	@Path("games/{id:\\d+}/turns")
	public Response endTurn(@PathParam("id") int gameId) {
		Game game = GameStore.INSTANCE.get(gameId);
		Player player = game.getPlayers().get(0);
		game.endTurn(player);

		return getNoCacheResponseBuilder(Response.Status.OK).build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("games/{id:\\d+}/universe/travellingShipFormations")
	public Response sendShips(@PathParam("id") int gameId, ShipFormation ships) {
		Game game = GameStore.INSTANCE.get(gameId);
		Player player = game.getPlayers().get(0);
		try {
			game.getUniverse().sendShips(ships.shipCount, ships.originPlanetId, ships.destinationPlanetId, player);
			return getNoCacheResponseBuilder(Response.Status.OK).build();
		} catch (IllegalActionException e) {
			return getNoCacheResponseBuilder(Response.Status.CONFLICT).build();
		}
	}

	@POST
	@Path("games/{id:\\d+}/universe/planets/{planetId:\\d+}/factories")
	public Response buildFactory(@PathParam("id") int gameId, @PathParam("planetId") int planetId) {
		Game game = GameStore.INSTANCE.get(gameId);
		Player player = game.getPlayers().get(0);

		try {
			game.getUniverse().getPlanetForId(planetId).buildFactory(player);
		} catch (IllegalActionException e) {
			return getNoCacheResponseBuilder(Response.Status.CONFLICT).build();
		}

		return getNoCacheResponseBuilder(Response.Status.OK).build();
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
		Player p = new Player("Player 1");
		Player pc = new ComputerPlayer("Computer");
		game.addPlayer(p);
		game.addPlayer(pc);
		game.setUniverse(UniverseGenerator.generate(Arrays.asList(p, pc)));
		game.start();
		return game;
	}
}
