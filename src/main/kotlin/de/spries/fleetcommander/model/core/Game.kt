package de.spries.fleetcommander.model.core

import de.spries.fleetcommander.model.core.common.IllegalActionException
import de.spries.fleetcommander.model.core.universe.Universe
import de.spries.fleetcommander.model.core.universe.UniverseFactory

open class Game(val universeGenerator: ((List<Player>) -> (Universe)) = { UniverseFactory.generate(it) }) {

    private var id: Int = 0
    private val players = mutableListOf<Player>()
    private var universe: Universe? = null
    private var status = Status.PENDING
    private var previousTurnEvents: TurnEvents? = null
    private var nextPlayerId = 1
    private var turnNumber = 0

    enum class Status {
        PENDING,
        RUNNING,
        OVER
    }

    fun getId() = id
    fun assignId(id: Int) {
        this.id = id
    }

    fun getPlayers() = players
    fun getUniverse() = universe
    fun getStatus() = status
    fun getPreviousTurnEvents() = previousTurnEvents
    fun getTurnNumber() = turnNumber

    fun addPlayer(player: Player) {
        if (Status.PENDING != status) {
            throw IllegalActionException("It's too late to add players")
        }
        if (players.size >= MAX_PLAYERS) {
            throw IllegalActionException("Limit of $MAX_PLAYERS players reached")
        }
        if (players.contains(player)) {
            throw IllegalActionException("There is already a player named " + player.getName())
        }

        assignPlayerId(player)
        players.add(player)
    }

    @Synchronized
    private fun assignPlayerId(player: Player) {
        player.assignId(nextPlayerId++)
    }

    fun start(player: Player) {
        if (!players.contains(player)) {
            throw IllegalActionException("You are not participating in this game")
        }
        if (Status.PENDING != status) {
            throw IllegalActionException("The game has started already")
        }
        if (players.size < 2) {
            throw IllegalActionException("At least 2 players required to start the game!")
        }

        player.setReady()
        tryStart()
    }

    private fun tryStart() {
        if (countReadyPlayers() == players.size) {
            start()
        }
    }

    fun start() {
        turnNumber++
        previousTurnEvents = TurnEvents(players)
        universe = universeGenerator.invoke(players)
        universe!!.setEventBus(previousTurnEvents!!)
        status = Status.RUNNING

        resetReadyStatusOnPlayers()
        notifyActivePlayersForNewTurn()
    }

    //TODO players should not be able to make any actions between ending their turn and the actual end of the turn
    fun endTurn(player: Player) {
        if (!players.contains(player)) {
            throw IllegalActionException("$player doesn't participate in this game and therefore cannot end the turn")
        }
        if (!player.isActive()) {
            throw IllegalActionException("$player has been defeated and therefore cannot end the turn")
        }

        player.setReady()
        tryEndTurn()
    }

    private fun tryEndTurn() {
        if (countReadyPlayers() == countActivePlayers()) {
            endTurn()
        }
    }

    fun endTurn() {
        if (Status.PENDING == status) {
            throw IllegalActionException("Game is not in progress, yet")
        }

        turnNumber++
        previousTurnEvents!!.clear()
        resetReadyStatusOnPlayers()
        universe!!.resetPreviousTurnMarkers()
        universe!!.runFactoryProductionCycle()
        universe!!.runShipTravellingCycle()

        val newDefeatedPlayers = players.filter { p -> p.isActive() }
                .filter { p -> null == universe!!.getHomePlanetOf(p) }
        handleNewDefeatedPlayers(newDefeatedPlayers)

        val numActivePlayers = players.filter { p -> p.isActive() }.count()
        val numActiveHumanPlayers = countActiveHumanPlayers()
        if (numActivePlayers <= 1 || numActiveHumanPlayers < 1) {
            status = Status.OVER
        }

        if (Status.OVER != status) {
            notifyActivePlayersForNewTurn()
        }
    }

    fun quit(player: Player) {
        if (!players.contains(player)) {
            throw IllegalActionException("$player doesn't participate in this game")
        }

        if (Status.PENDING == status) {
            players.remove(player)
        } else if (Status.RUNNING == status) {
            player.handleQuit()
            handleNewDefeatedPlayer(player)
            tryEndTurn()
        }

        if (countActiveHumanPlayers() < 1) {
            status = Status.OVER
        }
    }

    private fun handleNewDefeatedPlayers(newDefeatedPlayers: Collection<Player>) {
        newDefeatedPlayers.forEach { p -> handleNewDefeatedPlayer(p) }
    }

    private fun handleNewDefeatedPlayer(newDefeatedPlayer: Player) {
        newDefeatedPlayer.handleDefeat()
        universe!!.handleDefeatedPlayer(newDefeatedPlayer)
    }

    private fun notifyActivePlayersForNewTurn() {
        players.filter { it.isActive() }.forEach { p -> p.notifyNewTurn(this) }
    }

    private fun resetReadyStatusOnPlayers() {
        players.filter { p -> p.isReady() }.forEach { p -> p.setPlaying() }
    }

    private fun countActiveHumanPlayers(): Int {
        return players.filter { p -> p.isActive() && p.isHumanPlayer() }.count()
    }

    private fun countActivePlayers(): Int {
        return players.filter { p -> p.isActive() }.count()
    }

    private fun countReadyPlayers(): Int {
        return players.filter { p -> p.isReady() }.count()
    }

    fun getPlayerWithId(playerId: Int): Player? {
        return players.firstOrNull { p -> p.getId() == playerId }
    }

    companion object {

        const val MAX_PLAYERS = 6
    }

}
