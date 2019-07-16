package de.spries.fleetcommander.model.facade

import de.spries.fleetcommander.model.core.Player

open class OtherPlayer(protected var originalPlayer: Player) {

    fun getName() = originalPlayer.getName()

    fun getStatus() = originalPlayer.getStatus()

    fun isHumanPlayer() = originalPlayer.isHumanPlayer()
}
