package de.spries.fleetcommander.service.rest

import de.spries.fleetcommander.persistence.InvalidCodeException
import de.spries.fleetcommander.persistence.JoinCodeLimitReachedException
import de.spries.fleetcommander.service.core.GamesService
import de.spries.fleetcommander.service.core.dto.GameParams
import de.spries.fleetcommander.service.core.dto.GamePlayer
import de.spries.fleetcommander.service.core.dto.ShipFormationParams
import de.spries.fleetcommander.service.rest.errorhandling.RestError
import javax.ws.rs.Consumes
import javax.ws.rs.DELETE
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.CacheControl
import javax.ws.rs.core.Context
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("")
class GamesRestService {

    data class NewGameParams(val playerName: String, val joinCode: String? = null)
    data class JoinCodes(val joinCodes: Collection<String>)
    data class PlanetParams(val productionFocus: Int = 0)

    @POST
    @Path("games")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun createOrJoinGame(params: NewGameParams?): Response {
        if (params == null) {
            return noCacheResponse(Response.Status.BAD_REQUEST).entity(
                    RestError("Parameter 'playerName' is required"))
                    .build()
        }

        return if (params.joinCode == null) {
            createGame(params.playerName)
        } else joinGame(params.playerName, params.joinCode)
    }

    private fun createGame(playerName: String): Response {
        val accessParams = SERVICE.createNewGame(playerName)
        return noCacheResponse(Response.Status.CREATED)
                .header("Location", "/api/games/" + accessParams.gameId)
                .entity(accessParams).build()
    }

    private fun joinGame(playerName: String, joinCode: String): Response {
        try {
            val accessParams = SERVICE.joinGame(playerName, joinCode)

            return noCacheResponse(Response.Status.CREATED)
                    .header("Location", "/api/games/" + accessParams.gameId)
                    .entity(accessParams).build()
        } catch (e: InvalidCodeException) {
            return noCacheResponse(Response.Status.NOT_FOUND).entity(RestError(e.message))
                    .build()
        }

    }

    @GET
    @Path("games/{id:\\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    fun getGame(@PathParam("id") gameId: Int, @Context headers: HttpHeaders): Response {
        val playerId = GameAccessTokenFilter.extractPlayerIdFromHeaders(headers)
        val gameView = SERVICE.getGame(GamePlayer.forIds(gameId, playerId))
        return noCacheResponse(Response.Status.OK).entity(gameView).build()
    }

    @POST
    @Path("games/{id:\\d+}/joinCodes")
    @Produces(MediaType.APPLICATION_JSON)
    fun createJoinCode(@PathParam("id") gameId: Int): Response {
        try {
            SERVICE.createJoinCode(gameId)
            return noCacheResponse(Response.Status.CREATED)
                    .header("Location", "/api/games/$gameId/joinCodes")
                    .build()
        } catch (e: JoinCodeLimitReachedException) {
            return noCacheResponse(Response.Status.CONFLICT).entity(RestError(e.message))
                    .build()
        }

    }

    @GET
    @Path("games/{id:\\d+}/joinCodes")
    @Produces(MediaType.APPLICATION_JSON)
    fun getActiveJoinCodes(@PathParam("id") gameId: Int): Response {
        val codes = SERVICE.getActiveJoinCodes(gameId)
        return noCacheResponse(Response.Status.OK).entity(JoinCodes(codes)).build()
    }

    @DELETE
    @Path("games/{id:\\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    fun quitGame(@PathParam("id") gameId: Int, @Context headers: HttpHeaders): Response {
        val playerId = GameAccessTokenFilter.extractPlayerIdFromHeaders(headers)
        SERVICE.quitGame(GamePlayer.forIds(gameId, playerId))
        return noCacheResponse(Response.Status.ACCEPTED).build()
    }

    @POST
    @Path("games/{id:\\d+}/players")
    @Produces(MediaType.APPLICATION_JSON)
    fun addComputerPlayer(@PathParam("id") gameId: Int, @Context headers: HttpHeaders): Response {
        val playerId = GameAccessTokenFilter.extractPlayerIdFromHeaders(headers)
        SERVICE.addComputerPlayer(GamePlayer.forIds(gameId, playerId))
        return noCacheResponse(Response.Status.ACCEPTED).build()
    }

    @POST
    @Path("games/{id:\\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    fun startGame(@PathParam("id") gameId: Int, params: GameParams, @Context headers: HttpHeaders): Response {
        val playerId = GameAccessTokenFilter.extractPlayerIdFromHeaders(headers)
        SERVICE.modifyGame(GamePlayer.forIds(gameId, playerId), params)
        return noCacheResponse(Response.Status.ACCEPTED).build()
    }

    @POST
    @Path("games/{id:\\d+}/turns")
    @Produces(MediaType.APPLICATION_JSON)
    fun endTurn(@PathParam("id") gameId: Int, @Context headers: HttpHeaders): Response {
        val playerId = GameAccessTokenFilter.extractPlayerIdFromHeaders(headers)
        SERVICE.endTurn(GamePlayer.forIds(gameId, playerId))
        return noCacheResponse(Response.Status.ACCEPTED).build()
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("games/{id:\\d+}/universe/travellingShipFormations")
    @Produces(MediaType.APPLICATION_JSON)
    fun sendShips(@PathParam("id") gameId: Int, ships: ShipFormationParams, @Context headers: HttpHeaders): Response {
        val playerId = GameAccessTokenFilter.extractPlayerIdFromHeaders(headers)
        SERVICE.sendShips(GamePlayer.forIds(gameId, playerId), ships)
        return noCacheResponse(Response.Status.ACCEPTED).build()
    }

    @POST
    @Path("games/{id:\\d+}/universe/planets/{planetId:\\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    fun modifyPlanet(@PathParam("id") gameId: Int, @PathParam("planetId") planetId: Int, params: PlanetParams,
                     @Context headers: HttpHeaders): Response {
        val playerId = GameAccessTokenFilter.extractPlayerIdFromHeaders(headers)
        SERVICE.changePlanetProductionFocus(GamePlayer.forIds(gameId, playerId), planetId, params.productionFocus)
        return noCacheResponse(Response.Status.ACCEPTED).build()
    }

    @POST
    @Path("games/{id:\\d+}/universe/planets/{planetId:\\d+}/factories")
    @Produces(MediaType.APPLICATION_JSON)
    fun buildFactory(@PathParam("id") gameId: Int, @PathParam("planetId") planetId: Int,
                     @Context headers: HttpHeaders): Response {
        val playerId = GameAccessTokenFilter.extractPlayerIdFromHeaders(headers)
        SERVICE.buildFactory(GamePlayer.forIds(gameId, playerId), planetId)
        return noCacheResponse(Response.Status.ACCEPTED).build()
    }

    companion object {

        private val SERVICE = GamesService()

        fun noCacheResponse(status: Response.Status): Response.ResponseBuilder {
            val cc = CacheControl()
            cc.isNoCache = true
            cc.maxAge = -1
            cc.isMustRevalidate = true

            return Response.status(status).cacheControl(cc)
        }
    }
}
