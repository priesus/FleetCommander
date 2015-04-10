package de.spries.fleetcommander.rest;

import java.security.GeneralSecurityException;
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
import de.spries.fleetcommander.model.common.IllegalActionException;
import de.spries.fleetcommander.model.player.ComputerPlayer;
import de.spries.fleetcommander.model.player.Player;
import de.spries.fleetcommander.model.universe.UniverseGenerator;
import de.spries.fleetcommander.persistence.GameStore;

@Path("")
public class GameService {

	//TODO move authorization check into a filter to avoid code duplication

	private static final String AUTH_TOKEN_PREFIX = "Bearer ";

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
	public Response getGame(@PathParam("id") int id, @Context HttpHeaders httpHeaders) {
		String token = extractToken(httpHeaders);
		if (!GameAuthenticator.INSTANCE.isAuthTokenValid(id, token)) {
			return getNoCacheResponseBuilder(Response.Status.UNAUTHORIZED).build();
		}

		Game game = GameStore.INSTANCE.get(id);
		return getNoCacheResponseBuilder(Response.Status.OK).entity(game).build();
	}

	@DELETE
	@Path("games/{id:\\d+}")
	public Response quitGame(@PathParam("id") int id, @Context HttpHeaders httpHeaders) {
		String token = extractToken(httpHeaders);

		try {
			GameAuthenticator.INSTANCE.deleteAuthToken(id, token);
		} catch (GeneralSecurityException e) {
			return getNoCacheResponseBuilder(Response.Status.UNAUTHORIZED).build();
		}
		GameStore.INSTANCE.delete(id);
		return getNoCacheResponseBuilder(Response.Status.OK).build();
	}

	@POST
	@Path("games/{id:\\d+}/turns")
	public Response endTurn(@PathParam("id") int gameId, @Context HttpHeaders httpHeaders) {
		String token = extractToken(httpHeaders);
		if (!GameAuthenticator.INSTANCE.isAuthTokenValid(gameId, token)) {
			return getNoCacheResponseBuilder(Response.Status.UNAUTHORIZED).build();
		}

		Game game = GameStore.INSTANCE.get(gameId);
		Player player = game.getPlayers().get(0);
		game.endTurn(player);

		return getNoCacheResponseBuilder(Response.Status.OK).build();
	}

	// TODO accept parameters as JSON
	@POST
	@Path("games/{id:\\d+}/universe/travellingShipFormations/{ships:\\d+}/{origin:\\d+}/{dest:\\d+}")
	public Response sendShips(@PathParam("id") int gameId, @PathParam("ships") int shipCount,
			@PathParam("origin") int originPlanetId, @PathParam("dest") int destinationPlanetId,
			@Context HttpHeaders httpHeaders) {

		String token = extractToken(httpHeaders);
		if (!GameAuthenticator.INSTANCE.isAuthTokenValid(gameId, token)) {
			return getNoCacheResponseBuilder(Response.Status.UNAUTHORIZED).build();
		}

		Game game = GameStore.INSTANCE.get(gameId);
		Player player = game.getPlayers().get(0);
		try {
			game.getUniverse().sendShips(shipCount, originPlanetId, destinationPlanetId, player);
			return getNoCacheResponseBuilder(Response.Status.OK).build();
		} catch (IllegalActionException e) {
			return getNoCacheResponseBuilder(Response.Status.CONFLICT).build();
		}
	}

	@POST
	@Path("games/{id:\\d+}/universe/planets/{planetId:\\d+}/factories")
	public Response buildFactory(@PathParam("id") int gameId, @PathParam("planetId") int planetId,
			@Context HttpHeaders httpHeaders) {
		String token = extractToken(httpHeaders);
		if (!GameAuthenticator.INSTANCE.isAuthTokenValid(gameId, token)) {
			return getNoCacheResponseBuilder(Response.Status.UNAUTHORIZED).build();
		}

		Game game = GameStore.INSTANCE.get(gameId);
		Player player = game.getPlayers().get(0);

		try {
			game.getUniverse().getPlanetForId(planetId).buildFactory(player);
		} catch (Exception e) {
			return getNoCacheResponseBuilder(Response.Status.CONFLICT).build();
		}

		return getNoCacheResponseBuilder(Response.Status.OK).build();
	}

	private String extractToken(HttpHeaders httpHeaders) {
		String token = httpHeaders.getHeaderString("Authorization");
		if (token != null && token.startsWith(AUTH_TOKEN_PREFIX)) {
			return token.substring(AUTH_TOKEN_PREFIX.length());
		}
		return null;
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
