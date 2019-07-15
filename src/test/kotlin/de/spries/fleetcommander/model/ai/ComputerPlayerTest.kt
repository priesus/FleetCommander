package de.spries.fleetcommander.model.ai

import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertThat
import org.mockito.Matchers.anyInt
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

import de.spries.fleetcommander.model.ai.behavior.ProductionStrategy
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

import de.spries.fleetcommander.model.ai.behavior.BuildingStrategy
import de.spries.fleetcommander.model.ai.behavior.FleetStrategy
import de.spries.fleetcommander.model.core.Game
import de.spries.fleetcommander.model.core.Player.Status
import de.spries.fleetcommander.model.core.universe.Universe
import de.spries.fleetcommander.model.facade.PlayerSpecificUniverse

class ComputerPlayerTest {

    private lateinit var player: ComputerPlayer
    private lateinit var game: Game
    private lateinit var universe: Universe
    private lateinit var buildingStrategy: BuildingStrategy
    private lateinit var fleetStrategy: FleetStrategy
    private lateinit var prodStrategy: ProductionStrategy

    @Before
    fun setUp() {
        buildingStrategy = mock(BuildingStrategy::class.java)
        fleetStrategy = mock(FleetStrategy::class.java)
        prodStrategy = mock(ProductionStrategy::class.java)
        player = ComputerPlayer("Computer", buildingStrategy!!, fleetStrategy!!, prodStrategy!!)

        game = mock(Game::class.java)
        universe = mock(Universe::class.java)

        doReturn(universe).`when`(game).universe
    }

    @Test
    @Throws(Exception::class)
    fun isntAHumanPlayer() {
        assertThat(player!!.isHumanPlayer(), `is`(false))
    }

    @Test
    @Throws(Exception::class)
    fun isReadyInitially() {
        assertThat(player!!.status, `is`(Status.READY))
    }

    @Test
    @Throws(Exception::class)
    fun callsBuildingStrategy() {
        player!!.notifyNewTurn(game!!)
        verify(buildingStrategy).buildFactories(Mockito.any(PlayerSpecificUniverse::class.java))
    }

    @Test
    @Throws(Exception::class)
    fun callsFleetStrategy() {
        player!!.notifyNewTurn(game!!)
        verify(fleetStrategy).sendShips(Mockito.any(PlayerSpecificUniverse::class.java))
    }

    @Test
    @Throws(Exception::class)
    fun callsProductionStrategy() {
        player!!.notifyNewTurn(game!!)
        verify(prodStrategy).updateProductionFocus(Mockito.any(PlayerSpecificUniverse::class.java), anyInt())
    }

    @Test
    fun endsTurn() {
        player!!.notifyNewTurn(game!!)
        verify(game).endTurn(player!!)
    }

    @Test
    @Throws(Exception::class)
    fun stillEndsTurnWhenPlayerHasNoMoreHomePlanet() {
        doThrow(RuntimeException::class.java).`when`(universe).getHomePlanetOf(player!!)
        player!!.notifyNewTurn(game!!)
        verify(game).endTurn(player!!)
    }

    @Test
    @Throws(Exception::class)
    fun endsTurnIfFleetStrategyThrowsException() {
        doThrow(RuntimeException::class.java).`when`(fleetStrategy).sendShips(Mockito.any(PlayerSpecificUniverse::class.java))
        player!!.notifyNewTurn(game!!)
        verify(game).endTurn(player!!)
    }

    @Test
    @Throws(Exception::class)
    fun endsTurnIfBuildingStrategyThrowsException() {
        doThrow(RuntimeException::class.java).`when`(buildingStrategy)
                .buildFactories(Mockito.any(PlayerSpecificUniverse::class.java))
        player!!.notifyNewTurn(game!!)
        verify(game).endTurn(player!!)
    }

}
