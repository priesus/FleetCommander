package de.spries.fleetcommander.model.core

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import de.spries.fleetcommander.model.core.Game.Status
import de.spries.fleetcommander.model.core.common.IllegalActionException
import de.spries.fleetcommander.model.core.universe.Planet
import de.spries.fleetcommander.model.core.universe.Universe
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.hasItem
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.inOrder
import org.mockito.Mockito.never
import org.mockito.Mockito.verify

class StartedGameTest {

    private lateinit var startedGame: Game
    private lateinit var universe: Universe
    private lateinit var jack: Player
    private lateinit var john: Player
    private lateinit var computerPlayer: Player
    private lateinit var computerPlayer2: Player
    private lateinit var otherPlayer: Player
    private lateinit var someHomePlanet: Planet

    @Before
    @Throws(Exception::class)
    fun setUp() {
        john = mock()
        jack = mock()
        computerPlayer = mock()
        computerPlayer2 = mock()
        otherPlayer = mock()

        whenever(john.isHumanPlayer()).thenReturn(true)
        whenever(jack.isHumanPlayer()).thenReturn(true)
        whenever(computerPlayer.isHumanPlayer()).thenReturn(false)
        whenever(computerPlayer2.isHumanPlayer()).thenReturn(false)

        val universeMock = mock<Universe>()
        universe = universeMock

        startedGame = Game(universeGenerator = { universeMock })
        startedGame.addPlayer(john)
        startedGame.addPlayer(jack)
        startedGame.addPlayer(computerPlayer)
        startedGame.addPlayer(computerPlayer2)
        startedGame.start()

        someHomePlanet = mock()
    }

    @Test
    @Throws(Exception::class)
    fun statusIsRunningAfterGameStarted() {
        assertThat(startedGame.status, `is`(Status.RUNNING))
    }

    @Test
    @Throws(Exception::class)
    fun turnNumberIsOneInitially() {
        assertThat(startedGame.turnNumber, `is`(1))
    }

    @Test
    @Throws(Exception::class)
    fun turnNumberIncreasesWithEndedTurns() {
        startedGame.endTurn()
        assertThat(startedGame.turnNumber, `is`(2))
        startedGame.endTurn()
        assertThat(startedGame.turnNumber, `is`(3))
    }

    @Test(expected = IllegalActionException::class)
    @Throws(Exception::class)
    fun cannotStartGameTwice() {
        startedGame.start(john)
    }

    @Test
    @Throws(Exception::class)
    fun gameHasAUniverse() {
        assertThat(startedGame.universe, `is`(universe))
    }

    @Test(expected = IllegalActionException::class)
    @Throws(Exception::class)
    fun cannotAddPlayersAfterGameHasStarted() {
        startedGame.addPlayer(otherPlayer)
    }

    @Test
    @Throws(Exception::class)
    fun endingTurnRunsFactoryCycle() {
        startedGame.endTurn()
        verify(universe).runFactoryProductionCycle()
    }

    @Test
    @Throws(Exception::class)
    fun endingTurnResetsPreviousTurnMarkersBeforeShipsTravel() {
        startedGame.endTurn()
        val inOrder = inOrder(universe)
        inOrder.verify(universe).resetPreviousTurnMarkers()
        inOrder.verify(universe).runShipTravellingCycle()
    }

    @Test
    @Throws(Exception::class)
    fun endingTurnRunsProductionCycleBeforeShipsTravel() {
        startedGame.endTurn()
        val inOrder = inOrder(universe)
        inOrder.verify(universe).runFactoryProductionCycle()
        inOrder.verify(universe).runShipTravellingCycle()
    }

    @Test
    @Throws(Exception::class)
    fun endingTurnRunsShipTravellingCycle() {
        startedGame.endTurn()
        verify(universe).runShipTravellingCycle()
    }

    @Test(expected = IllegalActionException::class)
    @Throws(Exception::class)
    fun playerThatDoesntParticipateCannotEndTurn() {
        startedGame.endTurn(otherPlayer)
    }

    @Test
    @Throws(Exception::class)
    fun turnDoesntEndBeforeAllPlayersHaveEndedTheirTurn() {
        doReturn(true).`when`(john).isActive()
        doReturn(true).`when`(jack).isActive()
        doReturn(false).`when`(jack).isReady()
        startedGame.endTurn(john)

        verify(universe, never()).runFactoryProductionCycle()
        verify(universe, never()).runShipTravellingCycle()
    }

    @Test(expected = IllegalActionException::class)
    @Throws(Exception::class)
    fun defeatedPlayersCannotEndTurn() {
        doReturn(false).`when`(john).isActive()
        startedGame.endTurn(john)
    }

    @Test
    @Throws(Exception::class)
    fun turnEndsAfterAllPlayersHaveEndedTheirTurn() {
        doReturn(true).`when`(john).isActive()
        doReturn(true).`when`(jack).isActive()
        doReturn(true).`when`(john).isReady()
        doReturn(true).`when`(jack).isReady()
        startedGame.endTurn(jack)

        verify(universe).runFactoryProductionCycle()
        verify(universe).runShipTravellingCycle()
    }

    @Test
    @Throws(Exception::class)
    fun turnEndsAfterAllActivePlayersHaveEndedTheirTurn() {
        doReturn(true).`when`(john).isActive()
        doReturn(false).`when`(jack).isActive()
        doReturn(true).`when`(john).isReady()
        doReturn(false).`when`(jack).isReady()

        startedGame.endTurn(john)

        verify(universe).runFactoryProductionCycle()
        verify(universe).runShipTravellingCycle()
    }

    @Test
    @Throws(Exception::class)
    fun activePlayersAreNotifiedOfTurnEnd() {
        doReturn(true).`when`(john).isActive()
        doReturn(true).`when`(jack).isActive()
        doReturn(true).`when`(computerPlayer).isActive()
        doReturn(true).`when`(computerPlayer2).isActive()
        startedGame.endTurn()

        verify(john).notifyNewTurn(startedGame)
        verify(jack).notifyNewTurn(startedGame)
        verify(computerPlayer).notifyNewTurn(startedGame)
        verify(computerPlayer2).notifyNewTurn(startedGame)
    }

    @Test
    @Throws(Exception::class)
    fun readyPlayersAreChangedToPlayingAtTurnEnd() {
        doReturn(true).`when`(john).isReady()
        doReturn(true).`when`(jack).isReady()
        doReturn(true).`when`(computerPlayer).isReady()
        doReturn(true).`when`(computerPlayer2).isReady()
        startedGame.endTurn()

        verify(john).setPlaying()
        verify(jack).setPlaying()
        verify(computerPlayer).setPlaying()
        verify(computerPlayer2).setPlaying()
    }

    @Test
    @Throws(Exception::class)
    fun defeatedPlayersAreNotNotifiedOfTurnEnd() {
        doReturn(true).`when`(john).isActive()
        doReturn(false).`when`(jack).isActive()
        doReturn(false).`when`(computerPlayer).isActive()
        doReturn(true).`when`(computerPlayer2).isActive()
        startedGame.endTurn()

        verify(john).notifyNewTurn(startedGame)
        verify(jack, never()).notifyNewTurn(startedGame)
        verify(computerPlayer, never()).notifyNewTurn(startedGame)
        verify(computerPlayer2).notifyNewTurn(startedGame)
    }

    @Test
    @Throws(Exception::class)
    fun setsNewDefeatedPlayersInactive() {
        doReturn(true).`when`(john).isActive()
        doReturn(false).`when`(jack).isActive()
        whenever(universe.getHomePlanetOf(john)).thenReturn(null)
        whenever(universe.getHomePlanetOf(jack)).thenReturn(null)
        startedGame.endTurn()

        verify(john).handleDefeat()
        verify(jack, never()).handleDefeat()
    }

    @Test
    @Throws(Exception::class)
    fun notifiesUniverseForDefeatedPlayers() {
        doReturn(true).`when`(john).isActive()
        doReturn(true).`when`(jack).isActive()
        whenever(universe.getHomePlanetOf(john)).thenReturn(someHomePlanet)
        whenever(universe.getHomePlanetOf(jack)).thenReturn(null)
        startedGame.endTurn()

        verify(universe).handleDefeatedPlayer(jack)
        verify(universe, never()).handleDefeatedPlayer(john)
    }

    @Test
    @Throws(Exception::class)
    fun gameOverWhenLastPlayerIsHuman() {
        doReturn(true).`when`(john).isActive()
        doReturn(false).`when`(jack).isActive()
        doReturn(false).`when`(computerPlayer).isActive()
        doReturn(false).`when`(computerPlayer2).isActive()
        startedGame.endTurn()

        assertThat(startedGame.status, `is`(Status.OVER))
    }

    @Test
    @Throws(Exception::class)
    fun gameOverWhenNoHumanPlayersLeft() {
        doReturn(false).`when`(john).isActive()
        doReturn(false).`when`(jack).isActive()
        doReturn(true).`when`(computerPlayer).isActive()
        doReturn(true).`when`(computerPlayer2).isActive()
        startedGame.endTurn()

        assertThat(startedGame.status, `is`(Status.OVER))
    }

    @Test
    @Throws(Exception::class)
    fun noPlayersNotifiedAfterGameEnd() {
        doReturn(true).`when`(john).isActive()
        doReturn(false).`when`(jack).isActive()
        doReturn(false).`when`(computerPlayer).isActive()
        doReturn(false).`when`(computerPlayer2).isActive()
        startedGame.endTurn()

        verify(john, never()).notifyNewTurn(startedGame)
        verify(jack, never()).notifyNewTurn(startedGame)
        verify(computerPlayer, never()).notifyNewTurn(startedGame)
    }

    @Test
    @Throws(Exception::class)
    fun quittingPlayerBecomesInactive() {
        doReturn(true).`when`(john).isActive()
        startedGame.quit(john)
        verify(john).handleDefeat()
        assertThat<List<Player>>(startedGame.players, hasItem(john))
    }

    @Test
    @Throws(Exception::class)
    fun quittingPlayerIsTreatedAsDefeated() {
        doReturn(true).`when`(john).isActive()
        startedGame.quit(john)
        verify(universe).handleDefeatedPlayer(john)
    }

    @Test
    @Throws(Exception::class)
    fun quittingEndsTurnIfPlayerWasOnlyPlayerStillPlaying() {
        doReturn(true).`when`(john).isActive()
        doReturn(true).`when`(jack).isActive()
        doReturn(true).`when`(computerPlayer).isActive()
        doReturn(true).`when`(computerPlayer2).isActive()

        doReturn(true).`when`(jack).isReady()
        doReturn(true).`when`(computerPlayer).isReady()
        doReturn(true).`when`(computerPlayer2).isReady()
        verify(jack, never()).notifyNewTurn(startedGame)

        doReturn(false).`when`(john).isActive()
        startedGame.quit(john)

        verify(jack).notifyNewTurn(startedGame)
    }

    @Test
    @Throws(Exception::class)
    fun quittingEndsGameIfLastHumanPlayerLeft() {
        doReturn(true).`when`(computerPlayer).isActive()
        doReturn(true).`when`(computerPlayer2).isActive()

        doReturn(false).`when`(john).isActive()
        startedGame.quit(john)

        doReturn(false).`when`(jack).isActive()
        startedGame.quit(jack)
        assertThat(startedGame.status, `is`(Status.OVER))
    }

    @Test
    @Throws(Exception::class)
    fun quittingDoesntEndGameIfActiveHumanPlayersLeft() {
        doReturn(true).`when`(john).isActive()
        doReturn(true).`when`(jack).isActive()
        doReturn(true).`when`(computerPlayer).isActive()
        doReturn(true).`when`(computerPlayer2).isActive()
        startedGame.quit(john)
        assertThat(startedGame.status, `is`(Status.RUNNING))
    }

}
