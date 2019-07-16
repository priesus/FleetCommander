package de.spries.fleetcommander.service.rest

import de.spries.fleetcommander.model.facade.PlayerSpecificGame
import de.spries.fleetcommander.service.core.GamesService
import de.spries.fleetcommander.service.core.dto.GameAccessParams
import de.spries.fleetcommander.service.core.dto.GameParams
import de.spries.fleetcommander.service.core.dto.GamePlayer
import de.spries.fleetcommander.service.core.dto.ShipFormationParams
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletResponse

@RestController
class GamesController {

    data class NewGameParams(var playerName: String, var joinCode: String? = null)
    data class JoinCodes(var joinCodes: Collection<String>)
    data class PlanetParams(var productionFocus: Int = 0)

    @PostMapping("games")
    fun createOrJoinGame(@RequestBody params: NewGameParams, response: HttpServletResponse): GameAccessParams {
        val accessParams = if (params.joinCode == null) {
            SERVICE.createNewGame(params.playerName)
        } else {
            SERVICE.joinGame(params.playerName, params.joinCode!!)
        }
        response.addHeader("Location", "/api/games/" + accessParams.getGameId())
        return accessParams
    }

    @GetMapping("games/{id:\\d+}")
    fun getGame(@PathVariable("id") gameId: Int, @RequestHeader headers: HttpHeaders): PlayerSpecificGame {
        val playerId = GameAccessTokenFilter.extractPlayerIdFromHeaders(headers)
        return SERVICE.getGame(GamePlayer(gameId, playerId))
    }

    @PostMapping("games/{id:\\d+}/join-codes")
    fun createJoinCode(@PathVariable("id") gameId: Int, response: HttpServletResponse) {
        SERVICE.createJoinCode(gameId)
        response.addHeader("Location", "/api/games/$gameId/joinCodes")
    }

    @GetMapping("games/{id:\\d+}/join-codes")
    fun getActiveJoinCodes(@PathVariable("id") gameId: Int): JoinCodes {
        val codes = SERVICE.getActiveJoinCodes(gameId)
        return JoinCodes(codes)
    }

    @DeleteMapping("games/{id:\\d+}")
    fun quitGame(@PathVariable("id") gameId: Int, @RequestHeader headers: HttpHeaders) {
        val playerId = GameAccessTokenFilter.extractPlayerIdFromHeaders(headers)
        SERVICE.quitGame(GamePlayer(gameId, playerId))
    }

    @PostMapping("games/{id:\\d+}/players")
    fun addComputerPlayer(@PathVariable("id") gameId: Int, @RequestHeader headers: HttpHeaders) {
        val playerId = GameAccessTokenFilter.extractPlayerIdFromHeaders(headers)
        SERVICE.addComputerPlayer(GamePlayer(gameId, playerId))
    }

    @PostMapping("games/{id:\\d+}")
    fun startGame(@PathVariable("id") gameId: Int, @RequestBody params: GameParams, @RequestHeader headers: HttpHeaders) {
        val playerId = GameAccessTokenFilter.extractPlayerIdFromHeaders(headers)
        SERVICE.modifyGame(GamePlayer(gameId, playerId), params)
    }

    @PostMapping("games/{id:\\d+}/turns")
    fun endTurn(@PathVariable("id") gameId: Int, @RequestHeader headers: HttpHeaders) {
        val playerId = GameAccessTokenFilter.extractPlayerIdFromHeaders(headers)
        SERVICE.endTurn(GamePlayer(gameId, playerId))
    }

    @PostMapping("games/{id:\\d+}/universe/travelling-ship-formations")
    fun sendShips(@PathVariable("id") gameId: Int, @RequestBody ships: ShipFormationParams, @RequestHeader headers: HttpHeaders) {
        val playerId = GameAccessTokenFilter.extractPlayerIdFromHeaders(headers)
        SERVICE.sendShips(GamePlayer(gameId, playerId), ships)
    }

    @PostMapping("games/{id:\\d+}/universe/planets/{planetId:\\d+}")
    fun modifyPlanet(@PathVariable("id") gameId: Int, @PathVariable("planetId") planetId: Int, @RequestBody params: PlanetParams,
                     @RequestHeader headers: HttpHeaders) {
        val playerId = GameAccessTokenFilter.extractPlayerIdFromHeaders(headers)
        SERVICE.changePlanetProductionFocus(GamePlayer(gameId, playerId), planetId, params.productionFocus)
    }

    @PostMapping("games/{id:\\d+}/universe/planets/{planetId:\\d+}/factories")
    fun buildFactory(@PathVariable("id") gameId: Int, @PathVariable("planetId") planetId: Int,
                     @RequestHeader headers: HttpHeaders) {
        val playerId = GameAccessTokenFilter.extractPlayerIdFromHeaders(headers)
        SERVICE.buildFactory(GamePlayer(gameId, playerId), planetId)
    }

    companion object {
        private val SERVICE = GamesService()
    }
}
