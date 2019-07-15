package de.spries.fleetcommander.model.core

import de.spries.fleetcommander.model.core.common.IllegalActionException
import de.spries.fleetcommander.model.core.universe.FactorySite

open class Player(val name: String, var id: Int = -1, var credits: Int = STARTING_CREDITS) {

    class InsufficientCreditsException(msg: String) : IllegalActionException(msg)

    var status = Status.PLAYING
        private set

    open fun isHumanPlayer(): Boolean {
        return true
    }

    fun isActive(): Boolean {
        return Status.PLAYING == status || Status.READY == status
    }

    fun isReady(): Boolean {
        return Status.READY == status
    }

    enum class Status {
        PLAYING,
        READY,
        DEFEATED,
        QUIT
    }

    fun handleDefeat() {
        status = Status.DEFEATED
    }

    fun setReady() {
        if (Status.READY == status) {
            throw IllegalActionException("You have to wait for the other players")
        }
        status = Status.READY
    }

    fun setPlaying() {
        if (Status.QUIT == status || Status.DEFEATED == status) {
            throw IllegalActionException("You're out!")
        }
        status = Status.PLAYING
    }

    fun handleQuit() {
        if (Status.QUIT == status) {
            throw IllegalActionException("You quit the game already")
        }
        status = Status.QUIT
    }

    fun canAffordFactory(): Boolean {
        return credits >= FactorySite.FACTORY_COST
    }

    fun reduceCredits(debit: Int) {
        if (debit > credits) {
            throw InsufficientCreditsException("You don't have sufficient credits!")
        }
        credits -= debit
    }

    fun addCredits(creditsToAdd: Int) {
        credits += creditsToAdd
        if (credits > MAX_CREDITS) {
            credits = MAX_CREDITS
        }
    }

    open fun notifyNewTurn(game: Game) {
        //Nothing to do
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Player

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun toString(): String {
        return name
    }

    companion object {

        const val STARTING_CREDITS = 500
        const val MAX_CREDITS = 99999

        fun filterAllOtherPlayers(players: List<Player>, viewingPlayer: Player): List<Player> {
            return players.filter { p -> p != viewingPlayer }
        }
    }
}
