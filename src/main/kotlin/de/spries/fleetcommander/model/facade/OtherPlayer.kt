package de.spries.fleetcommander.model.facade

import de.spries.fleetcommander.model.core.Player

open class OtherPlayer(protected var originalPlayer: Player) {

    fun getName() = originalPlayer.name

    fun getStatus() = originalPlayer.status

    fun isHumanPlayer() = originalPlayer.isHumanPlayer()
}
