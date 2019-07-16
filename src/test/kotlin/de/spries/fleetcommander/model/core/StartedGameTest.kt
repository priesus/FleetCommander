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
    fun statusIsRunningAfterGameStarted() {
        assertThat(startedGame.getStatus(), `is`(Status.RUNNING))
    }

    @Test
    fun turnNumberIsOneInitially() {
        assertThat(startedGame.getTurnNumber(), `is`(1))
    }

    @Test
    fun turnNumberIncreasesWithEndedTurns() {
        startedGame.endTurn()
        assertThat(startedGame.getTurnNumber(), `is`(2))
        startedGame.endTurn()
        assertThat(startedGame.getTurnNumber(), `is`(3))
    }

    @Test(expected = IllegalActionException::class)
    fun cannotStartGameTwice() {
        startedGame.start(john)
    }

    @Test
    fun gameHasAUniverse() {
        assertThat(startedGame.getUniverse(), `is`(universe))
    }

    @Test(expected = IllegalActionException::class)
    fun cannotAddPlayersAfterGameHasStarted() {
        startedGame.addPlayer(otherPlayer)
    }

    @Test
    fun endingTurnRunsFactoryCycle() {
        startedGame.endTurn()
        verify(universe).runFactoryProductionCycle()
    }

    @Test
    fun endingTurnResetsPreviousTurnMarkersBeforeShipsTravel() {
        startedGame.endTurn()
        val inOrder = inOrder(universe)
        inOrder.verify(universe).resetPreviousTurnMarkers()
        inOrder.verify(universe).runShipTravellingCycle()
    }

    @Test
    fun endingTurnRunsProductionCycleBeforeShipsTravel() {
        startedGame.endTurn()
        val inOrder = inOrder(universe)
        inOrder.verify(universe).runFactoryProductionCycle()
        inOrder.verify(universe).runShipTravellingCycle()
    }

    @Test
    fun endingTurnRunsShipTravellingCycle() {
        startedGame.endTurn()
        verify(universe).runShipTravellingCycle()
    }

    @Test(expected = IllegalActionException::class)
    fun playerThatDoesntParticipateCannotEndTurn() {
        startedGame.endTurn(otherPlayer)
    }

    @Test
    fun turnDoesntEndBeforeAllPlayersHaveEndedTheirTurn() {
        doReturn(true).`when`(john).isActive()
        doReturn(true).`when`(jack).isActive()
        doReturn(false).`when`(jack).isReady()
        startedGame.endTurn(john)

        verify(universe, never()).runFactoryProductionCycle()
        verify(universe, never()).runShipTravellingCycle()
    }

    @Test(expected = IllegalActionException::class)
    fun defeatedPlayersCannotEndTurn() {
        doReturn(false).`when`(john).isActive()
        startedGame.endTurn(john)
    }

    @Test
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
    fun gameOverWhenLastPlayerIsHuman() {
        doReturn(true).`when`(john).isActive()
        doReturn(false).`when`(jack).isActive()
        doReturn(false).`when`(computerPlayer).isActive()
        doReturn(false).`when`(computerPlayer2).isActive()
        startedGame.endTurn()

        assertThat(startedGame.getStatus(), `is`(Status.OVER))
    }

    @Test
    fun gameOverWhenNoHumanPlayersLeft() {
        doReturn(false).`when`(john).isActive()
        doReturn(false).`when`(jack).isActive()
        doReturn(true).`when`(computerPlayer).isActive()
        doReturn(true).`when`(computerPlayer2).isActive()
        startedGame.endTurn()

        assertThat(startedGame.getStatus(), `is`(Status.OVER))
    }

    @Test
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
    fun quittingPlayerBecomesInactive() {
        doReturn(true).`when`(john).isActive()
        startedGame.quit(john)
        verify(john).handleDefeat()
        assertThat<List<Player>>(startedGame.getPlayers(), hasItem(john))
    }

    @Test
    fun quittingPlayerIsTreatedAsDefeated() {
        doReturn(true).`when`(john).isActive()
        startedGame.quit(john)
        verify(universe).handleDefeatedPlayer(john)
    }

    @Test
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
    fun quittingEndsGameIfLastHumanPlayerLeft() {
        doReturn(true).`when`(computerPlayer).isActive()
        doReturn(true).`when`(computerPlayer2).isActive()

        doReturn(false).`when`(john).isActive()
        startedGame.quit(john)

        doReturn(false).`when`(jack).isActive()
        startedGame.quit(jack)
        assertThat(startedGame.getStatus(), `is`(Status.OVER))
    }

    @Test
    fun quittingDoesntEndGameIfActiveHumanPlayersLeft() {
        doReturn(true).`when`(john).isActive()
        doReturn(true).`when`(jack).isActive()
        doReturn(true).`when`(computerPlayer).isActive()
        doReturn(true).`when`(computerPlayer2).isActive()
        startedGame.quit(john)
        assertThat(startedGame.getStatus(), `is`(Status.RUNNING))
    }

}
