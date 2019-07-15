package de.spries.fleetcommander.model.facade

import de.spries.fleetcommander.model.core.Player

open class OwnPlayer(originalPlayer: Player) : OtherPlayer(originalPlayer) {

    fun getCredits() = originalPlayer.credits

    fun canAffordFactory() = originalPlayer.canAffordFactory()
}
