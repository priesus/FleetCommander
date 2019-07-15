package de.spries.fleetcommander.model.ai

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import de.spries.fleetcommander.model.ai.behavior.BuildingStrategy
import de.spries.fleetcommander.model.ai.behavior.FleetStrategy
import de.spries.fleetcommander.model.ai.behavior.ProductionStrategy
import de.spries.fleetcommander.model.core.Game
import de.spries.fleetcommander.model.core.Player.Status
import de.spries.fleetcommander.model.core.universe.Universe
import de.spries.fleetcommander.model.facade.PlayerSpecificUniverse
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Matchers.anyInt
import org.mockito.Mockito
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
    }

    @Test
    @Throws(Exception::class)
    fun isntAHumanPlayer() {
        assertThat(player.isHumanPlayer(), `is`(false))
    }

    @Test
    @Throws(Exception::class)
    fun isReadyInitially() {
        assertThat(player.status, `is`(Status.READY))
    }

    @Test
    @Throws(Exception::class)
    fun callsBuildingStrategy() {
        player.notifyNewTurn(game)
        verify(buildingStrategy).buildFactories(Mockito.any(PlayerSpecificUniverse::class.java))
    }

    @Test
    @Throws(Exception::class)
    fun callsFleetStrategy() {
        player.notifyNewTurn(game)
        verify(fleetStrategy).sendShips(Mockito.any(PlayerSpecificUniverse::class.java))
    }

    @Test
    @Throws(Exception::class)
    fun callsProductionStrategy() {
        player.notifyNewTurn(game)
        verify(prodStrategy).updateProductionFocus(Mockito.any(PlayerSpecificUniverse::class.java), anyInt())
    }

    @Test
    fun endsTurn() {
        player.notifyNewTurn(game)
        verify(game).endTurn(player)
    }

    @Test
    @Throws(Exception::class)
    fun stillEndsTurnWhenPlayerHasNoMoreHomePlanet() {
        whenever(universe.getHomePlanetOf(player)).thenThrow(RuntimeException())
        player.notifyNewTurn(game)
        verify(game).endTurn(player)
    }

    @Test
    @Throws(Exception::class)
    fun endsTurnIfFleetStrategyThrowsException() {
        whenever(fleetStrategy.sendShips(Mockito.any(PlayerSpecificUniverse::class.java))).thenThrow(RuntimeException())
        player.notifyNewTurn(game)
        verify(game).endTurn(player)
    }

    @Test
    @Throws(Exception::class)
    fun endsTurnIfBuildingStrategyThrowsException() {
        doThrow(RuntimeException::class.java).`when`(buildingStrategy)
                .buildFactories(Mockito.any(PlayerSpecificUniverse::class.java))
        player.notifyNewTurn(game)
        verify(game).endTurn(player)
    }

}
