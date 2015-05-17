package de.spries.fleetcommander.service.rest;

import java.util.Collection;

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
import de.spries.fleetcommander.persistence.InvalidCodeException;
import de.spries.fleetcommander.persistence.JoinCodeLimitReachedException;
import de.spries.fleetcommander.service.core.GamesService;
import de.spries.fleetcommander.service.core.dto.GameAccessParams;
import de.spries.fleetcommander.service.core.dto.GameParams;
import de.spries.fleetcommander.service.core.dto.GamePlayer;
import de.spries.fleetcommander.service.core.dto.ShipFormationParams;
import de.spries.fleetcommander.service.rest.errorhandling.RestError;

@Path("")
public class GamesRestService {

	public static class NewGameParams {
		public String playerName;
		public String joinCode;
	}

	public static class JoinCodes {
		public Collection<String> joinCodes;

		public JoinCodes(Collection<String> joinCodes) {
			this.joinCodes = joinCodes;
		}
	}

	private static final GamesService SERVICE = new GamesService();

	@POST
	@Path("games")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createOrJoinGame(NewGameParams params) {
		if (params == null || params.playerName == null) {
			return noCacheResponse(Response.Status.BAD_REQUEST).entity(
					new RestError("Parameter 'playerName' is required"))
					.build();
		}

		if (params.joinCode == null) {
			return createGame(params.playerName);
		}
		return joinGame(params.playerName, params.joinCode);
	}

	private Response createGame(String playerName) {
		GameAccessParams accessParams = SERVICE.createNewGame(playerName);
		return noCacheResponse(Response.Status.CREATED)
				.header("Location", "/rest/games/" + accessParams.getGameId())
				.entity(accessParams).build();
	}

	private Response joinGame(String playerName, String joinCode) {
		try {
			GameAccessParams accessParams = SERVICE.joinGame(playerName, joinCode);

			return noCacheResponse(Response.Status.CREATED)
					.header("Location", "/rest/games/" + accessParams.getGameId())
					.entity(accessParams).build();
		} catch (InvalidCodeException e) {
			return noCacheResponse(Response.Status.NOT_FOUND).entity(new RestError(e.getMessage()))
					.build();
		}
	}

	@GET
	@Path("games/{id:\\d+}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getGame(@PathParam("id") int gameId, @Context HttpHeaders headers) {
		int playerId = GameAccessTokenFilter.extractPlayerIdFromHeaders(headers);
		PlayerSpecificGame gameView = SERVICE.getGame(GamePlayer.forIds(gameId, playerId));
		return noCacheResponse(Response.Status.OK).entity(gameView).build();
	}

	@POST
	@Path("games/{id:\\d+}/joinCodes")
	@Produces(MediaType.APPLICATION_JSON)
	public Response createJoinCode(@PathParam("id") int gameId) {
		try {
			SERVICE.createJoinCode(gameId);
			return noCacheResponse(Response.Status.CREATED)
					.header("Location", "/rest/games/" + gameId + "/joinCodes")
					.build();
		} catch (JoinCodeLimitReachedException e) {
			return noCacheResponse(Response.Status.CONFLICT).entity(new RestError(e.getMessage()))
					.build();
		}
	}

	@GET
	@Path("games/{id:\\d+}/joinCodes")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getActiveJoinCodes(@PathParam("id") int gameId) {
		Collection<String> codes = SERVICE.getActiveJoinCodes(gameId);
		return noCacheResponse(Response.Status.OK).entity(new JoinCodes(codes)).build();
	}

	@DELETE
	@Path("games/{id:\\d+}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response quitGame(@PathParam("id") int gameId, @Context HttpHeaders headers) {
		int playerId = GameAccessTokenFilter.extractPlayerIdFromHeaders(headers);
		SERVICE.quitGame(GamePlayer.forIds(gameId, playerId));
		return noCacheResponse(Response.Status.ACCEPTED).build();
	}

	@POST
	@Path("games/{id:\\d+}/players")
	@Produces(MediaType.APPLICATION_JSON)
	public Response addComputerPlayer(@PathParam("id") int gameId, @Context HttpHeaders headers) {
		int playerId = GameAccessTokenFilter.extractPlayerIdFromHeaders(headers);
		SERVICE.addComputerPlayer(GamePlayer.forIds(gameId, playerId));
		return noCacheResponse(Response.Status.ACCEPTED).build();
	}

	@POST
	@Path("games/{id:\\d+}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response startGame(@PathParam("id") int gameId, GameParams params, @Context HttpHeaders headers) {
		int playerId = GameAccessTokenFilter.extractPlayerIdFromHeaders(headers);
		SERVICE.modifyGame(GamePlayer.forIds(gameId, playerId), params);
		return noCacheResponse(Response.Status.ACCEPTED).build();
	}

	@POST
	@Path("games/{id:\\d+}/turns")
	@Produces(MediaType.APPLICATION_JSON)
	public Response endTurn(@PathParam("id") int gameId, @Context HttpHeaders headers) {
		int playerId = GameAccessTokenFilter.extractPlayerIdFromHeaders(headers);
		SERVICE.endTurn(GamePlayer.forIds(gameId, playerId));
		return noCacheResponse(Response.Status.ACCEPTED).build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("games/{id:\\d+}/universe/travellingShipFormations")
	@Produces(MediaType.APPLICATION_JSON)
	public Response sendShips(@PathParam("id") int gameId, ShipFormationParams ships, @Context HttpHeaders headers) {
		int playerId = GameAccessTokenFilter.extractPlayerIdFromHeaders(headers);
		try {
			SERVICE.sendShips(GamePlayer.forIds(gameId, playerId), ships);
			return noCacheResponse(Response.Status.ACCEPTED).build();
		} catch (IllegalActionException e) {
			return noCacheResponse(Response.Status.CONFLICT).build();
		}
	}

	@POST
	@Path("games/{id:\\d+}/universe/planets/{planetId:\\d+}/factories")
	@Produces(MediaType.APPLICATION_JSON)
	public Response buildFactory(@PathParam("id") int gameId, @PathParam("planetId") int planetId,
			@Context HttpHeaders headers) {
		int playerId = GameAccessTokenFilter.extractPlayerIdFromHeaders(headers);
		SERVICE.buildFactory(GamePlayer.forIds(gameId, playerId), planetId);
		return noCacheResponse(Response.Status.ACCEPTED).build();
	}

	public static Response.ResponseBuilder noCacheResponse(Response.Status status) {
		CacheControl cc = new CacheControl();
		cc.setNoCache(true);
		cc.setMaxAge(-1);
		cc.setMustRevalidate(true);

		return Response.status(status).cacheControl(cc);
	}
}
