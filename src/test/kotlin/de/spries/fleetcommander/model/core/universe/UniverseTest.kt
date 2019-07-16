package de.spries.fleetcommander.model.core.universe

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import de.spries.fleetcommander.model.core.Player
import de.spries.fleetcommander.model.core.common.IllegalActionException
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.empty
import org.hamcrest.Matchers.hasItem
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.not
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.verify
import java.util.NoSuchElementException

class UniverseTest {
    private lateinit var john: Player
    private lateinit var jack: Player
    private lateinit var johnsHomePlanet: Planet
    private lateinit var jacksHomePlanet: Planet
    private lateinit var uninhabitedPlanet: Planet
    private lateinit var distantPlanet: Planet
    private lateinit var universe: Universe

    @Before
    fun setUp() {
        john = mock()
        jack = mock()
        johnsHomePlanet = mock()
        jacksHomePlanet = mock()
        uninhabitedPlanet = mock()
        distantPlanet = mock()
        universe = Universe(listOf(johnsHomePlanet, jacksHomePlanet, uninhabitedPlanet, distantPlanet))

        whenever(johnsHomePlanet.isHomePlanet()).thenReturn(true)
        whenever(jacksHomePlanet.isHomePlanet()).thenReturn(true)
    }

    @Test(expected = IllegalArgumentException::class)
    fun requiresNonEmptyPlanetList() {
        Universe(emptyList())
    }

    @Test
    fun newUniverseHasPlanets() {
        assertThat(universe.getPlanets(), `is`(not(emptyList())))
    }

    @Test
    @Throws(Exception::class)
    fun universeHasHomePlanets() {
        val homePlanets = universe.getHomePlanets()
        assertThat(homePlanets, hasItem(johnsHomePlanet))
        assertThat(homePlanets, hasItem(jacksHomePlanet))
        assertThat(homePlanets, hasSize(2))
    }

    @Test
    @Throws(Exception::class)
    fun universeHasNoTravellingShips() {
        assertThat<Collection<ShipFormation>>(universe.getTravellingShipFormations(), hasSize(0))
    }

    @Test
    @Throws(Exception::class)
    fun sendingShipsAddsTravellingShipsToUniverse() {
        universe.sendShips(1, johnsHomePlanet, uninhabitedPlanet, john)
        val shipFormations = universe.getTravellingShipFormations()
        assertThat<Collection<ShipFormation>>(shipFormations, hasSize(1))

        assertThat<Collection<ShipFormation>>(shipFormations, hasItem(ShipFormation(1, johnsHomePlanet, uninhabitedPlanet, john)))
    }

    @Test
    @Throws(Exception::class)
    fun sendingShipsAddsIncomingShipsToDestinatonPlanet() {
        universe.sendShips(3, johnsHomePlanet, uninhabitedPlanet, john)
        verify(uninhabitedPlanet).addIncomingShips(3, john)
    }

    @Test
    @Throws(Exception::class)
    fun sendingShipsToSameDestinationAgainIncreasesShipsTravelling() {
        universe.sendShips(1, johnsHomePlanet, uninhabitedPlanet, john)
        universe.sendShips(2, johnsHomePlanet, uninhabitedPlanet, john)
        val shipFormations = universe.getTravellingShipFormations()
        assertThat<Collection<ShipFormation>>(shipFormations, hasSize(1))

        assertThat<Collection<ShipFormation>>(shipFormations, hasItem(ShipFormation(3, johnsHomePlanet, uninhabitedPlanet, john)))
    }

    @Test
    @Throws(Exception::class)
    fun sendingShipsToDifferentDestinationAddsAnotherShipFormation() {
        universe.sendShips(1, johnsHomePlanet, uninhabitedPlanet, john)
        universe.sendShips(1, johnsHomePlanet, distantPlanet, john)
        val shipFormations = universe.getTravellingShipFormations()
        assertThat<Collection<ShipFormation>>(shipFormations, hasSize(2))
    }

    @Test
    @Throws(Exception::class)
    fun sendingShipsToSamePlanetDoesntAffectUniverseOrPlanetShipCount() {
        val shipsBefore = johnsHomePlanet.getShipCount()
        universe.sendShips(1, johnsHomePlanet, johnsHomePlanet, john)
        assertThat(johnsHomePlanet.getShipCount(), `is`(shipsBefore))
        assertThat<Collection<ShipFormation>>(universe.getTravellingShipFormations(), `is`(empty()))
    }

    @Test(expected = IllegalActionException::class)
    @Throws(Exception::class)
    fun originMustBeInsideUniverse() {
        universe.sendShips(1, mock(), uninhabitedPlanet, john)
    }

    @Test(expected = IllegalActionException::class)
    @Throws(Exception::class)
    fun destinationMustBeInsideUniverse() {
        universe.sendShips(1, johnsHomePlanet, mock(), john)
    }

    @Test
    @Throws(Exception::class)
    fun travellingToDistantPlanetTakesMultipleCycles() {
        whenever(johnsHomePlanet.distanceTo(distantPlanet)).thenReturn(15.0)
        universe.sendShips(1, johnsHomePlanet, distantPlanet, john)

        universe.runShipTravellingCycle()
        assertThat<Collection<ShipFormation>>(universe.getTravellingShipFormations(), hasSize(1))

        universe.runShipTravellingCycle()
        assertThat<Collection<ShipFormation>>(universe.getTravellingShipFormations(), hasSize(0))
    }

    @Test
    @Throws(Exception::class)
    fun shipsLandOnTargetPlanet() {
        universe.sendShips(1, johnsHomePlanet, uninhabitedPlanet, john)
        universe.runShipTravellingCycle()
        verify(uninhabitedPlanet).landShips(1, john)
    }

    @Test
    @Throws(Exception::class)
    fun runsFactoryProductionCycleOnEveryPlanet() {
        universe.runFactoryProductionCycle()
        verify(johnsHomePlanet).runProductionCycle()
        verify(jacksHomePlanet).runProductionCycle()
        verify(uninhabitedPlanet).runProductionCycle()
        verify(distantPlanet).runProductionCycle()
    }

    @Test
    @Throws(Exception::class)
    fun resetsPreviousTurnMarkersOnEveryPlanet() {
        universe.resetPreviousTurnMarkers()
        verify(johnsHomePlanet).resetMarkers()
        verify(jacksHomePlanet).resetMarkers()
        verify(uninhabitedPlanet).resetMarkers()
        verify(distantPlanet).resetMarkers()
    }

    @Test
    @Throws(Exception::class)
    fun planetsAreIdentifiedByIdForTravellingShips() {
        whenever(johnsHomePlanet.id).thenReturn(1)
        whenever(uninhabitedPlanet.id).thenReturn(2)

        universe.sendShips(1, 1, 2, john)

        val shipFormations = universe.getTravellingShipFormations()
        assertThat<Collection<ShipFormation>>(shipFormations, hasItem(ShipFormation(1, johnsHomePlanet, uninhabitedPlanet, john)))
    }

    @Test(expected = NoSuchElementException::class)
    @Throws(Exception::class)
    fun originPlanetIsInvalidId() {
        universe.sendShips(1, INEXISTENT_PLANET, uninhabitedPlanet.id, john)
    }

    @Test(expected = NoSuchElementException::class)
    @Throws(Exception::class)
    fun destinationPlanetIsInvalidId() {
        universe.sendShips(1, johnsHomePlanet.id, INEXISTENT_PLANET, john)
    }

    @Test
    @Throws(Exception::class)
    fun setsEventBusForAllContainedPlanets() {
        val eventBus = mock<TurnEventBus>()
        universe.setEventBus(eventBus)

        verify(johnsHomePlanet).setEventBus(eventBus)
        verify(jacksHomePlanet).setEventBus(eventBus)
        verify(uninhabitedPlanet).setEventBus(eventBus)
        verify(distantPlanet).setEventBus(eventBus)
    }

    @Test
    @Throws(Exception::class)
    fun forwardsHandleDefeatedPlayerToAllPlanets() {
        universe.handleDefeatedPlayer(john)

        verify(johnsHomePlanet).handleDefeatedPlayer(john)
        verify(jacksHomePlanet).handleDefeatedPlayer(john)
        verify(uninhabitedPlanet).handleDefeatedPlayer(john)
        verify(distantPlanet).handleDefeatedPlayer(john)
    }

    @Test
    @Throws(Exception::class)
    fun removesDefeatedPlayersTravellingShips() {
        universe.sendShips(1, johnsHomePlanet, uninhabitedPlanet, john)
        universe.sendShips(1, jacksHomePlanet, uninhabitedPlanet, jack)
        universe.handleDefeatedPlayer(john)

        val shipFormations = universe.getTravellingShipFormations()
        assertThat<Collection<ShipFormation>>(shipFormations, hasSize(1))
        assertThat<Collection<ShipFormation>>(shipFormations, hasItem(ShipFormation(1, jacksHomePlanet, uninhabitedPlanet, jack)))
    }

    companion object {

        private val INEXISTENT_PLANET = 123456789
    }
}
