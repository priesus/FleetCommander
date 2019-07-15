package de.spries.fleetcommander.model.facade

import de.spries.fleetcommander.model.core.Player
import de.spries.fleetcommander.model.core.Player.Status

open class OtherPlayer(protected var originalPlayer: Player) {

    val name: String
        get() = originalPlayer.name

    val status: Status
        get() = originalPlayer.status

    val isHumanPlayer: Boolean
        get() = originalPlayer.isHumanPlayer()

    companion object {

        fun convert(otherPlayers: List<Player>): List<OtherPlayer> {
            return otherPlayers
                    .map { p -> OtherPlayer(p) }
        }
    }

}
