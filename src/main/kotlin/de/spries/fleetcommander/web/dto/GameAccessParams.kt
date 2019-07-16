package de.spries.fleetcommander.web.dto

import com.fasterxml.jackson.annotation.JsonIgnore

data class GameAccessParams(@get:JsonIgnore
                            val gamePlayer: GamePlayer,
                            @get:JsonIgnore
                            val authToken: String) {

    fun getGameId() = gamePlayer.gameId

    @JsonIgnore
    fun getPlayerId() = gamePlayer.playerId

    fun getFullAuthToken() = "${getPlayerId()}:$authToken"

}
