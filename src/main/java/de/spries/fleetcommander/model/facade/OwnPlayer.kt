package de.spries.fleetcommander.model.facade

import de.spries.fleetcommander.model.core.Player

class OwnPlayer(originalPlayer: Player) : OtherPlayer(originalPlayer) {

    val credits: Int
        get() = originalPlayer.credits

    val canAffordFactory: Boolean
        get() = originalPlayer.canAffordFactory()
}
