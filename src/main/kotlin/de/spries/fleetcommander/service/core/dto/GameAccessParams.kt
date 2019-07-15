package de.spries.fleetcommander.service.core.dto

import com.fasterxml.jackson.annotation.JsonIgnore

data class GameAccessParams(@get:JsonIgnore
                            val gamePlayer: GamePlayer,
                            @get:JsonIgnore
                            val authToken: String) {

    val gameId: Int
        get() = gamePlayer.gameId

    val playerId: Int
        @JsonIgnore
        get() = gamePlayer.playerId

    val fullAuthToken: String
        get() = "$playerId:$authToken"

}
