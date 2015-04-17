package de.spries.fleetcommander.service.rest;

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

import de.spries.fleetcommander.model.core.common.IllegalActionException;
import de.spries.fleetcommander.model.facade.PlayerSpecificGame;
import de.spries.fleetcommander.service.core.GameAccessParams;
import de.spries.fleetcommander.service.core.GamesService;

@Path("")
public class GamesRestService {

	private static final GamesService SERVICE = new GamesService();

	@POST
	@Path("games")
	public Response createGame() {
		GameAccessParams accessParams = SERVICE.createNewGame("Player 1");

		return getNoCacheResponseBuilder(Response.Status.CREATED)
				.header("Location", "/rest/games/" + accessParams.getGameId())
				.entity(accessParams.toJson()).build();
	}

	@GET
	@Path("games/{id:\\d+}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getGame(@PathParam("id") int id) {
		//TODO use player id
		String playerId = "abcdefghijk";
		PlayerSpecificGame gameView = SERVICE.getGame(id, playerId);
		return getNoCacheResponseBuilder(Response.Status.OK).entity(gameView).build();
	}

	@DELETE
	@Path("games/{id:\\d+}")
	public Response quitGame(@PathParam("id") int id, @Context HttpHeaders httpHeaders) {
		String token = GameAccessTokenFilter.extractAuthTokenFromHeaders(httpHeaders);
		SERVICE.deleteGame(id, token);
		return getNoCacheResponseBuilder(Response.Status.OK).build();
	}

	@POST
	@Path("games/{id:\\d+}/turns")
	public Response endTurn(@PathParam("id") int gameId) {
		PlayerSpecificGame game = SERVICE.getGame(gameId, null);
		game.endTurn();
		return getNoCacheResponseBuilder(Response.Status.OK).build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("games/{id:\\d+}/universe/travellingShipFormations")
	public Response sendShips(@PathParam("id") int gameId, ShipFormationParams ships) {
		PlayerSpecificGame game = SERVICE.getGame(gameId, null);

		try {
			game.getUniverse().sendShips(ships.getShipCount(), ships.getOriginPlanetId(),
					ships.getDestinationPlanetId());
			return getNoCacheResponseBuilder(Response.Status.OK).build();
		} catch (IllegalActionException e) {
			return getNoCacheResponseBuilder(Response.Status.CONFLICT).build();
		}
	}

	@POST
	@Path("games/{id:\\d+}/universe/planets/{planetId:\\d+}/factories")
	public Response buildFactory(@PathParam("id") int gameId, @PathParam("planetId") int planetId) {
		PlayerSpecificGame game = SERVICE.getGame(gameId, null);

		try {
			game.getUniverse().getPlanet(planetId).buildFactory();
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
}
