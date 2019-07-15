package de.spries.fleetcommander.model.core.universe

import com.nhaarman.mockito_kotlin.mock
import de.spries.fleetcommander.model.core.Player
import de.spries.fleetcommander.model.core.common.IllegalActionException
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.greaterThan
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.reset
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions

class PlanetShipsTest {

    private lateinit var jack: Player
    private lateinit var john: Player
    private lateinit var johnsHomePlanet: Planet
    private lateinit var jacksHomePlanet: Planet
    private lateinit var jacksPlanet: Planet
    private lateinit var uninhabitedPlanet: Planet
    private lateinit var eventBus: TurnEventBus

    @Before
    fun setUp() {
        john = mock()
        jack = mock()
        eventBus = mock()

        johnsHomePlanet = Planet(0, 0, john)
        jacksHomePlanet = Planet(1, 1, jack)
        uninhabitedPlanet = Planet(0, 0)
        jacksHomePlanet.setEventBus(eventBus)
        uninhabitedPlanet.setEventBus(eventBus)
        jacksPlanet = Planet(0, 0)
        jacksPlanet.setEventBus(eventBus)
        jacksPlanet.landShips(1, jack)

        reset(eventBus)
    }

    @Test
    @Throws(Exception::class)
    fun homePlanetStartsWithShips() {
        assertThat(johnsHomePlanet.getShipCount(), `is`(greaterThan(0)))
    }

    @Test
    @Throws(Exception::class)
    fun uninhabitedPlanetStartsWithoutShips() {
        assertThat(uninhabitedPlanet.getShipCount(), `is`(0))
    }

    @Test(expected = IllegalActionException::class)
    @Throws(Exception::class)
    fun cannotSendShipsFromOtherPlayersPlanets() {
        jacksHomePlanet.sendShipsAway(1, john)
    }

    @Test(expected = IllegalActionException::class)
    @Throws(Exception::class)
    fun cannotSendShipsFromUninhabitedPlanets() {
        uninhabitedPlanet.sendShipsAway(1, john)
    }

    @Test(expected = IllegalActionException::class)
    @Throws(Exception::class)
    fun cannotSendMoreShipsThanLocatedOnPlanet() {
        val shipCount = johnsHomePlanet.getShipCount()
        johnsHomePlanet.sendShipsAway(shipCount + 1, john)
    }

    @Test
    @Throws(Exception::class)
    fun sendingShipsReducedShipsCountOnPlanet() {
        val shipsBefore = johnsHomePlanet.getShipCount()
        johnsHomePlanet.sendShipsAway(1, john)

        assertThat(johnsHomePlanet.getShipCount(), `is`(shipsBefore - 1))
    }

    @Test
    @Throws(Exception::class)
    fun landingShipsIncreaseShipCount() {
        val shipsBefore = johnsHomePlanet.getShipCount()
        johnsHomePlanet.landShips(1, john)
        assertThat(johnsHomePlanet.getShipCount(), `is`(shipsBefore + 1))
    }

    @Test
    @Throws(Exception::class)
    fun landingShipsOnUninhabitedPlanetInhabitsPlanet() {
        uninhabitedPlanet.landShips(1, john)
        assertThat(uninhabitedPlanet.isInhabitedBy(john), `is`(true))
        assertThat(uninhabitedPlanet.getShipCount(), `is`(1))
    }

    @Test(expected = IllegalActionException::class)
    @Throws(Exception::class)
    fun landingZeroShipsDoesNotInhabitPlanet() {
        uninhabitedPlanet.landShips(0, john)
    }

    @Test
    @Throws(Exception::class)
    fun invadedPlanetIsNotHomePlanet() {
        uninhabitedPlanet.landShips(1, john)
        assertThat(uninhabitedPlanet.isHomePlanetOf(john), `is`(false))
    }

    @Test
    @Throws(Exception::class)
    fun planetHasNoShipsIncomingInitially() {
        assertThat(uninhabitedPlanet.getIncomingShipCount(john), `is`(0))
        assertThat(uninhabitedPlanet.getIncomingShipCount(jack), `is`(0))
    }

    @Test
    @Throws(Exception::class)
    fun addingIncomingShipsIncreasesIncomingShips() {
        uninhabitedPlanet.addIncomingShips(1, john)
        assertThat(uninhabitedPlanet.getIncomingShipCount(john), `is`(1))

        uninhabitedPlanet.addIncomingShips(1, jack)
        uninhabitedPlanet.addIncomingShips(2, john)
        assertThat(uninhabitedPlanet.getIncomingShipCount(john), `is`(3))
        assertThat(uninhabitedPlanet.getIncomingShipCount(jack), `is`(1))
    }

    @Test
    @Throws(Exception::class)
    fun landingShipsReducesIncomingShips() {
        uninhabitedPlanet.addIncomingShips(2, john)
        uninhabitedPlanet.addIncomingShips(2, jack)

        uninhabitedPlanet.landShips(1, john)
        assertThat(uninhabitedPlanet.getIncomingShipCount(john), `is`(1))
        assertThat(uninhabitedPlanet.getIncomingShipCount(jack), `is`(2))

        uninhabitedPlanet.landShips(1, john)
        assertThat(uninhabitedPlanet.getIncomingShipCount(john), `is`(0))
        assertThat(uninhabitedPlanet.getIncomingShipCount(jack), `is`(2))
    }

    @Test
    @Throws(Exception::class)
    fun attackingWithSomeShipsReducesEnemyShips() {
        jacksHomePlanet.landShips(1, john)
        assertThat(jacksHomePlanet.getShipCount(), `is`(5))
        assertThat(jacksHomePlanet.inhabitant, `is`(jack))

        jacksHomePlanet.landShips(2, john)
        assertThat(jacksHomePlanet.getShipCount(), `is`(3))
        assertThat(jacksHomePlanet.inhabitant, `is`(jack))
    }

    @Test
    @Throws(Exception::class)
    fun attackingWithEqualNumberOfShipsDestroysAllEnemyShips() {
        jacksHomePlanet.landShips(6, john)
        assertThat(jacksHomePlanet.getShipCount(), `is`(0))
        assertThat(jacksHomePlanet.inhabitant, `is`(jack))
    }

    @Test
    @Throws(Exception::class)
    fun attackingWithMoreShipsInhabitsEnemyPlanet() {
        jacksHomePlanet.landShips(7, john)
        assertThat(jacksHomePlanet.getShipCount(), `is`(1))
        assertThat(jacksHomePlanet.inhabitant, `is`(john))
    }

    @Test
    @Throws(Exception::class)
    fun invadingEnemyHomePlanetConvertsIntoRegularPlanet() {
        assertThat(jacksHomePlanet.isHomePlanet, `is`(true))
        jacksHomePlanet.landShips(7, john)
        assertThat(jacksHomePlanet.isHomePlanet, `is`(false))
    }

    @Test
    @Throws(Exception::class)
    fun ownPlanetIsNotEnemyPlanet() {
        assertThat(johnsHomePlanet.isKnownAsEnemyPlanet(john), `is`(false))
    }

    @Test
    @Throws(Exception::class)
    fun enemyPlanetIsNotRecognizedBeforeVisited() {
        assertThat(jacksHomePlanet.isKnownAsEnemyPlanet(john), `is`(false))
    }

    @Test
    @Throws(Exception::class)
    fun enemyPlanetIsRecognizedAfterUnsuccessfulAttack() {
        jacksHomePlanet.landShips(1, john)
        assertThat(jacksHomePlanet.isKnownAsEnemyPlanet(john), `is`(true))
    }

    @Test
    @Throws(Exception::class)
    fun enemyPlanetIsNoMoreEnemyAfterSuccessfulAttack() {
        jacksHomePlanet.landShips(1, john)
        jacksHomePlanet.landShips(7, john)
        assertThat(jacksHomePlanet.isKnownAsEnemyPlanet(john), `is`(false))
    }

    @Test
    @Throws(Exception::class)
    fun successfullyInvadedPlanetBecomesEnemyPlanetForInvadedPlayer() {
        jacksPlanet.landShips(5, john)
        assertThat(jacksPlanet.inhabitant, `is`(john))
        assertThat(jacksPlanet.isKnownAsEnemyPlanet(jack), `is`(true))
    }

    @Test
    @Throws(Exception::class)
    fun invadingLostPlanetIsNotKnownAsEnemyPlanet() {
        jacksPlanet.landShips(1, john)
        jacksPlanet.handleDefeatedPlayer(jack)
        jacksPlanet.landShips(1, john)
        assertThat(jacksPlanet.isKnownAsEnemyPlanet(john), `is`(false))
    }

    @Test
    @Throws(Exception::class)
    fun conqueringUninhabitedPlanetFiresEvent() {
        uninhabitedPlanet.landShips(1, john)
        verify(eventBus).fireConqueredUninhabitedPlanet(john)
        verifyNoMoreInteractions(eventBus)
    }

    @Test
    @Throws(Exception::class)
    fun conqueringEnemyPlanetFiresEventsForBothPlayers() {
        jacksHomePlanet.landShips(20, john)
        verify(eventBus).fireConqueredEnemyPlanet(john)
        verify(eventBus).fireLostPlanet(jack)
        verifyNoMoreInteractions(eventBus)
    }

    @Test
    @Throws(Exception::class)
    fun defendingPlanetFiresEventsForBothPlayers() {
        jacksHomePlanet.landShips(1, john)
        verify(eventBus).fireLostShipFormation(john)
        verify(eventBus).fireDefendedPlanet(jack)
        verifyNoMoreInteractions(eventBus)
    }

    @Test
    @Throws(Exception::class)
    fun planetIsNotUnderAttackInitially() {
        assertThat(uninhabitedPlanet.isUnderAttack, `is`(false))
    }

    @Test
    @Throws(Exception::class)
    fun planetWasNotAttackedWhenItWasFirstInhabited() {
        uninhabitedPlanet.landShips(1, john)
        assertThat(uninhabitedPlanet.isUnderAttack, `is`(false))
    }

    @Test
    @Throws(Exception::class)
    fun planetWasNotAttackedWhenSuccessfullyInvaded() {
        uninhabitedPlanet.landShips(1, john)
        uninhabitedPlanet.landShips(2, jack)
        assertThat(uninhabitedPlanet.isUnderAttack, `is`(false))
    }

    @Test
    @Throws(Exception::class)
    fun planetWasAttackedWhenUnsuccessfullyInvaded() {
        uninhabitedPlanet.landShips(2, john)
        uninhabitedPlanet.landShips(1, jack)
        assertThat(uninhabitedPlanet.isUnderAttack, `is`(true))
    }

    @Test
    @Throws(Exception::class)
    fun planetIsNotUnderAttackAfterResetMarkers() {
        uninhabitedPlanet.landShips(2, john)
        uninhabitedPlanet.landShips(1, jack)
        uninhabitedPlanet.resetMarkers()
        assertThat(uninhabitedPlanet.isUnderAttack, `is`(false))
    }

    @Test
    @Throws(Exception::class)
    fun planetIsNotJustInhabitedInitially() {
        assertThat(uninhabitedPlanet.isJustInhabited, `is`(false))
    }

    @Test
    @Throws(Exception::class)
    fun planetIsJustInhabitedWhenItWasFirstInhabited() {
        uninhabitedPlanet.landShips(1, john)
        assertThat(uninhabitedPlanet.isJustInhabited, `is`(true))
    }

    @Test
    @Throws(Exception::class)
    fun planetIsJustInhabitedWhenSuccessfullyInvaded() {
        uninhabitedPlanet.landShips(1, john)
        uninhabitedPlanet.landShips(2, jack)
        assertThat(uninhabitedPlanet.isJustInhabited, `is`(true))
    }

    @Test
    @Throws(Exception::class)
    fun planetIsNotJustInhabitedWhenUnsuccessfullyInvaded() {
        uninhabitedPlanet.landShips(2, john)
        uninhabitedPlanet.resetMarkers()
        uninhabitedPlanet.landShips(1, jack)
        assertThat(uninhabitedPlanet.isJustInhabited, `is`(false))
    }

    @Test
    @Throws(Exception::class)
    fun planetIsNotJustInhabitedAfterResetMarkers() {
        uninhabitedPlanet.landShips(1, john)
        uninhabitedPlanet.resetMarkers()
        assertThat(uninhabitedPlanet.isJustInhabited, `is`(false))
    }
}
