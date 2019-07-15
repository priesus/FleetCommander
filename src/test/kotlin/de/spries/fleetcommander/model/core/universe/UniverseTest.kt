package de.spries.fleetcommander.model.core.universe

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
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.util.Arrays
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
        john = mock(Player::class.java)
        jack = mock(Player::class.java)
        johnsHomePlanet = mock(Planet::class.java)
        jacksHomePlanet = mock(Planet::class.java)
        uninhabitedPlanet = mock(Planet::class.java)
        distantPlanet = mock(Planet::class.java)
        doReturn(true).`when`(johnsHomePlanet).isHomePlanet
        doReturn(true).`when`(jacksHomePlanet).isHomePlanet
        universe = Universe(Arrays.asList(johnsHomePlanet, jacksHomePlanet, uninhabitedPlanet, distantPlanet))
    }

    @Test(expected = IllegalArgumentException::class)
    fun requiresNonEmptyPlanetList() {
        Universe(emptyList())
    }

    @Test
    fun newUniverseHasPlanets() {
        assertThat(universe!!.planets, `is`(not(emptyList())))
    }

    @Test
    @Throws(Exception::class)
    fun universeHasHomePlanets() {
        val homePlanets = universe!!.homePlanets
        assertThat(homePlanets, hasItem(johnsHomePlanet))
        assertThat(homePlanets, hasItem(jacksHomePlanet))
        assertThat(homePlanets, hasSize(2))
    }

    @Test
    @Throws(Exception::class)
    fun universeHasNoTravellingShips() {
        assertThat<Collection<ShipFormation>>(universe!!.travellingShipFormations, hasSize(0))
    }

    @Test
    @Throws(Exception::class)
    fun sendingShipsAddsTravellingShipsToUniverse() {
        universe!!.sendShips(1, johnsHomePlanet!!, uninhabitedPlanet!!, john!!)
        val shipFormations = universe!!.travellingShipFormations
        assertThat<Collection<ShipFormation>>(shipFormations, hasSize(1))

        assertThat<Collection<ShipFormation>>(shipFormations, hasItem(ShipFormation(1, johnsHomePlanet!!, uninhabitedPlanet!!, john!!)))
    }

    @Test
    @Throws(Exception::class)
    fun sendingShipsAddsIncomingShipsToDestinatonPlanet() {
        universe!!.sendShips(3, johnsHomePlanet!!, uninhabitedPlanet!!, john!!)
        verify(uninhabitedPlanet).addIncomingShips(3, john!!)
    }

    @Test
    @Throws(Exception::class)
    fun sendingShipsToSameDestinationAgainIncreasesShipsTravelling() {
        universe!!.sendShips(1, johnsHomePlanet!!, uninhabitedPlanet!!, john!!)
        universe!!.sendShips(2, johnsHomePlanet!!, uninhabitedPlanet!!, john!!)
        val shipFormations = universe!!.travellingShipFormations
        assertThat<Collection<ShipFormation>>(shipFormations, hasSize(1))

        assertThat<Collection<ShipFormation>>(shipFormations, hasItem(ShipFormation(3, johnsHomePlanet!!, uninhabitedPlanet!!, john!!)))
    }

    @Test
    @Throws(Exception::class)
    fun sendingShipsToDifferentDestinationAddsAnotherShipFormation() {
        universe!!.sendShips(1, johnsHomePlanet!!, uninhabitedPlanet!!, john!!)
        universe!!.sendShips(1, johnsHomePlanet!!, distantPlanet!!, john!!)
        val shipFormations = universe!!.travellingShipFormations
        assertThat<Collection<ShipFormation>>(shipFormations, hasSize(2))
    }

    @Test
    @Throws(Exception::class)
    fun sendingShipsToSamePlanetDoesntAffectUniverseOrPlanetShipCount() {
        val shipsBefore = johnsHomePlanet!!.getShipCount()
        universe!!.sendShips(1, johnsHomePlanet!!, johnsHomePlanet!!, john!!)
        assertThat(johnsHomePlanet!!.getShipCount(), `is`(shipsBefore))
        assertThat<Collection<ShipFormation>>(universe!!.travellingShipFormations, `is`(empty()))
    }

    @Test(expected = IllegalActionException::class)
    @Throws(Exception::class)
    fun originMustBeInsideUniverse() {
        universe!!.sendShips(1, mock(Planet::class.java), uninhabitedPlanet!!, john!!)
    }

    @Test(expected = IllegalActionException::class)
    @Throws(Exception::class)
    fun destinationMustBeInsideUniverse() {
        universe!!.sendShips(1, johnsHomePlanet!!, mock(Planet::class.java), john!!)
    }

    @Test
    @Throws(Exception::class)
    fun travellingToDistantPlanetTakesMultipleCycles() {
        doReturn(15.0).`when`(johnsHomePlanet).distanceTo(distantPlanet!!)
        universe!!.sendShips(1, johnsHomePlanet!!, distantPlanet!!, john!!)

        universe!!.runShipTravellingCycle()
        assertThat<Collection<ShipFormation>>(universe!!.travellingShipFormations, hasSize(1))

        universe!!.runShipTravellingCycle()
        assertThat<Collection<ShipFormation>>(universe!!.travellingShipFormations, hasSize(0))
    }

    @Test
    @Throws(Exception::class)
    fun shipsLandOnTargetPlanet() {
        universe!!.sendShips(1, johnsHomePlanet!!, uninhabitedPlanet!!, john!!)
        universe!!.runShipTravellingCycle()
        verify(uninhabitedPlanet).landShips(1, john!!)
    }

    @Test
    @Throws(Exception::class)
    fun runsFactoryProductionCycleOnEveryPlanet() {
        universe!!.runFactoryProductionCycle()
        verify(johnsHomePlanet).runProductionCycle()
        verify(jacksHomePlanet).runProductionCycle()
        verify(uninhabitedPlanet).runProductionCycle()
        verify(distantPlanet).runProductionCycle()
    }

    @Test
    @Throws(Exception::class)
    fun resetsPreviousTurnMarkersOnEveryPlanet() {
        universe!!.resetPreviousTurnMarkers()
        verify(johnsHomePlanet).resetMarkers()
        verify(jacksHomePlanet).resetMarkers()
        verify(uninhabitedPlanet).resetMarkers()
        verify(distantPlanet).resetMarkers()
    }

    @Test
    @Throws(Exception::class)
    fun planetsAreIdentifiedByIdForTravellingShips() {
        doReturn(1).`when`(johnsHomePlanet).id
        doReturn(2).`when`(uninhabitedPlanet).id

        universe!!.sendShips(1, 1, 2, john!!)

        val shipFormations = universe!!.travellingShipFormations
        assertThat<Collection<ShipFormation>>(shipFormations, hasItem(ShipFormation(1, johnsHomePlanet!!, uninhabitedPlanet!!, john!!)))
    }

    @Test(expected = NoSuchElementException::class)
    @Throws(Exception::class)
    fun originPlanetIsInvalidId() {
        universe!!.sendShips(1, INEXISTENT_PLANET, uninhabitedPlanet!!.id, john!!)
    }

    @Test(expected = NoSuchElementException::class)
    @Throws(Exception::class)
    fun destinationPlanetIsInvalidId() {
        universe!!.sendShips(1, johnsHomePlanet!!.id, INEXISTENT_PLANET, john!!)
    }

    @Test
    @Throws(Exception::class)
    fun setsEventBusForAllContainedPlanets() {
        val eventBus = mock(TurnEventBus::class.java)
        universe!!.setEventBus(eventBus)

        verify(johnsHomePlanet).setEventBus(eventBus)
        verify(jacksHomePlanet).setEventBus(eventBus)
        verify(uninhabitedPlanet).setEventBus(eventBus)
        verify(distantPlanet).setEventBus(eventBus)
    }

    @Test
    @Throws(Exception::class)
    fun forwardsHandleDefeatedPlayerToAllPlanets() {
        universe!!.handleDefeatedPlayer(john!!)

        verify(johnsHomePlanet).handleDefeatedPlayer(john!!)
        verify(jacksHomePlanet).handleDefeatedPlayer(john!!)
        verify(uninhabitedPlanet).handleDefeatedPlayer(john!!)
        verify(distantPlanet).handleDefeatedPlayer(john!!)
    }

    @Test
    @Throws(Exception::class)
    fun removesDefeatedPlayersTravellingShips() {
        universe!!.sendShips(1, johnsHomePlanet!!, uninhabitedPlanet!!, john!!)
        universe!!.sendShips(1, jacksHomePlanet!!, uninhabitedPlanet!!, jack!!)
        universe!!.handleDefeatedPlayer(john!!)

        val shipFormations = universe!!.travellingShipFormations
        assertThat<Collection<ShipFormation>>(shipFormations, hasSize(1))
        assertThat<Collection<ShipFormation>>(shipFormations, hasItem(ShipFormation(1, jacksHomePlanet!!, uninhabitedPlanet!!, jack!!)))
    }

    companion object {

        private val INEXISTENT_PLANET = 123456789
    }
}
