package de.spries.fleetcommander.service.core.dto

data class GamePlayer(val gameId: Int, val playerId: Int) {

    companion object {

        fun forIds(gameId: Int, playerId: Int): GamePlayer {
            return GamePlayer(gameId, playerId)
        }
    }

}