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
import de.spries.fleetcommander.service.core.GamesService;
import de.spries.fleetcommander.service.core.dto.GameAccessParams;
import de.spries.fleetcommander.service.core.dto.GameParams;
import de.spries.fleetcommander.service.core.dto.ShipFormationParams;

@Path("")
public class GamesRestService {

	private static final GamesService SERVICE = new GamesService();

	@POST
	@Path("games")
	public Response createGame() {
		GameAccessParams accessParams = SERVICE.createNewGame("Player 1");

		return noCacheResponse(Response.Status.CREATED)
				.header("Location", "/rest/games/" + accessParams.getGameId())
				.entity(accessParams).build();
	}

	@GET
	@Path("games/{id:\\d+}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getGame(@PathParam("id") int id) {
		PlayerSpecificGame gameView = SERVICE.getGame(id);
		return noCacheResponse(Response.Status.OK).entity(gameView).build();
	}

	@DELETE
	@Path("games/{id:\\d+}")
	public Response quitGame(@PathParam("id") int id, @Context HttpHeaders httpHeaders) {
		String token = GameAccessTokenFilter.extractAuthTokenFromHeaders(httpHeaders);
		SERVICE.deleteGame(id, token);
		return noCacheResponse(Response.Status.ACCEPTED).build();
	}

	@POST
	@Path("games/{id:\\d+}/players")
	public Response addComputerPlayer(@PathParam("id") int gameId) {
		SERVICE.addComputerPlayer(gameId);
		return noCacheResponse(Response.Status.ACCEPTED).build();
	}

	@POST
	@Path("games/{id:\\d+}")
	public Response startGame(@PathParam("id") int gameId, GameParams params) {
		SERVICE.startGame(gameId, params);
		return noCacheResponse(Response.Status.ACCEPTED).build();
	}

	@POST
	@Path("games/{id:\\d+}/turns")
	public Response endTurn(@PathParam("id") int gameId) {
		SERVICE.endTurn(gameId);
		return noCacheResponse(Response.Status.ACCEPTED).build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("games/{id:\\d+}/universe/travellingShipFormations")
	public Response sendShips(@PathParam("id") int gameId, ShipFormationParams ships) {
		try {
			SERVICE.sendShips(gameId, ships);
			return noCacheResponse(Response.Status.ACCEPTED).build();
		} catch (IllegalActionException e) {
			return noCacheResponse(Response.Status.CONFLICT).build();
		}
	}

	@POST
	@Path("games/{id:\\d+}/universe/planets/{planetId:\\d+}/factories")
	public Response buildFactory(@PathParam("id") int gameId, @PathParam("planetId") int planetId) {
		try {
			SERVICE.buildFactory(gameId, planetId);
		} catch (IllegalActionException e) {
			return noCacheResponse(Response.Status.CONFLICT).build();
		}

		return noCacheResponse(Response.Status.ACCEPTED).build();
	}

	private Response.ResponseBuilder noCacheResponse(Response.Status status) {
		CacheControl cc = new CacheControl();
		cc.setNoCache(true);
		cc.setMaxAge(-1);
		cc.setMustRevalidate(true);

		return Response.status(status).cacheControl(cc);
	}
}
