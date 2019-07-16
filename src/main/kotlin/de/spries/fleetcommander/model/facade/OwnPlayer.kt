package de.spries.fleetcommander.model.facade

import de.spries.fleetcommander.model.core.Player

class OwnPlayer(originalPlayer: Player) : OtherPlayer(originalPlayer) {

    fun getCredits() = originalPlayer.getCredits()

    fun getCanAffordFactory() = originalPlayer.canAffordFactory()
}
