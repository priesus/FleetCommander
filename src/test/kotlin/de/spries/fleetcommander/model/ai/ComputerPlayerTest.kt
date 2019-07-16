package de.spries.fleetcommander.model.ai

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import de.spries.fleetcommander.model.ai.behavior.BuildingStrategy
import de.spries.fleetcommander.model.ai.behavior.FleetStrategy
import de.spries.fleetcommander.model.ai.behavior.ProductionStrategy
import de.spries.fleetcommander.model.core.Game
import de.spries.fleetcommander.model.core.Player.Status
import de.spries.fleetcommander.model.core.universe.Universe
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.verify

class ComputerPlayerTest {

    private lateinit var player: ComputerPlayer
    private lateinit var game: Game
    private lateinit var universe: Universe
    private lateinit var buildingStrategy: BuildingStrategy
    private lateinit var fleetStrategy: FleetStrategy
    private lateinit var prodStrategy: ProductionStrategy

    @Before
    fun setUp() {
        buildingStrategy = mock()
        fleetStrategy = mock()
        prodStrategy = mock()
        player = ComputerPlayer("Computer", buildingStrategy, fleetStrategy, prodStrategy)

        game = mock()
        universe = mock()
        whenever(game.getUniverse()).thenReturn(universe)
    }

    @Test
    fun isntAHumanPlayer() {
        assertThat(player.isHumanPlayer(), `is`(false))
    }

    @Test
    fun isReadyInitially() {
        assertThat(player.getStatus(), `is`(Status.READY))
    }

    @Test
    fun callsBuildingStrategy() {
        player.notifyNewTurn(game)
        verify(buildingStrategy).buildFactories(any())
    }

    @Test
    fun callsFleetStrategy() {
        player.notifyNewTurn(game)
        verify(fleetStrategy).sendShips(any())
    }

    @Test
    fun callsProductionStrategy() {
        player.notifyNewTurn(game)
        verify(prodStrategy).updateProductionFocus(any(), any())
    }

    @Test
    fun endsTurn() {
        player.notifyNewTurn(game)
        verify(game).endTurn(player)
    }

    @Test

    fun stillEndsTurnWhenPlayerHasNoMoreHomePlanet() {
        whenever(universe.getHomePlanetOf(player)).thenThrow(RuntimeException())
        player.notifyNewTurn(game)
        verify(game).endTurn(player)
    }

    @Test
    fun endsTurnIfFleetStrategyThrowsException() {
        whenever(fleetStrategy.sendShips(any())).thenThrow(RuntimeException())
        player.notifyNewTurn(game)
        verify(game).endTurn(player)
    }

    @Test
    fun endsTurnIfBuildingStrategyThrowsException() {
        doThrow(RuntimeException::class.java).`when`(buildingStrategy)
                .buildFactories(any())
        player.notifyNewTurn(game)
        verify(game).endTurn(player)
    }

}
