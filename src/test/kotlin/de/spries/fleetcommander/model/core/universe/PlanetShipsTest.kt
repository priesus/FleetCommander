package de.spries.fleetcommander.model.core.universe

import com.nhaarman.mockitokotlin2.mock
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
    fun homePlanetStartsWithShips() {
        assertThat(johnsHomePlanet.getShipCount(), `is`(greaterThan(0)))
    }

    @Test
    fun uninhabitedPlanetStartsWithoutShips() {
        assertThat(uninhabitedPlanet.getShipCount(), `is`(0))
    }

    @Test(expected = IllegalActionException::class)
    fun cannotSendShipsFromOtherPlayersPlanets() {
        jacksHomePlanet.sendShipsAway(1, john)
    }

    @Test(expected = IllegalActionException::class)
    fun cannotSendShipsFromUninhabitedPlanets() {
        uninhabitedPlanet.sendShipsAway(1, john)
    }

    @Test(expected = IllegalActionException::class)
    fun cannotSendMoreShipsThanLocatedOnPlanet() {
        val shipCount = johnsHomePlanet.getShipCount()
        johnsHomePlanet.sendShipsAway(shipCount + 1, john)
    }

    @Test
    fun sendingShipsReducedShipsCountOnPlanet() {
        val shipsBefore = johnsHomePlanet.getShipCount()
        johnsHomePlanet.sendShipsAway(1, john)

        assertThat(johnsHomePlanet.getShipCount(), `is`(shipsBefore - 1))
    }

    @Test
    fun landingShipsIncreaseShipCount() {
        val shipsBefore = johnsHomePlanet.getShipCount()
        johnsHomePlanet.landShips(1, john)
        assertThat(johnsHomePlanet.getShipCount(), `is`(shipsBefore + 1))
    }

    @Test
    fun landingShipsOnUninhabitedPlanetInhabitsPlanet() {
        uninhabitedPlanet.landShips(1, john)
        assertThat(uninhabitedPlanet.isInhabitedBy(john), `is`(true))
        assertThat(uninhabitedPlanet.getShipCount(), `is`(1))
    }

    @Test(expected = IllegalActionException::class)
    fun landingZeroShipsDoesNotInhabitPlanet() {
        uninhabitedPlanet.landShips(0, john)
    }

    @Test
    fun invadedPlanetIsNotHomePlanet() {
        uninhabitedPlanet.landShips(1, john)
        assertThat(uninhabitedPlanet.isHomePlanetOf(john), `is`(false))
    }

    @Test
    fun planetHasNoShipsIncomingInitially() {
        assertThat(uninhabitedPlanet.getIncomingShipCount(john), `is`(0))
        assertThat(uninhabitedPlanet.getIncomingShipCount(jack), `is`(0))
    }

    @Test
    fun addingIncomingShipsIncreasesIncomingShips() {
        uninhabitedPlanet.addIncomingShips(1, john)
        assertThat(uninhabitedPlanet.getIncomingShipCount(john), `is`(1))

        uninhabitedPlanet.addIncomingShips(1, jack)
        uninhabitedPlanet.addIncomingShips(2, john)
        assertThat(uninhabitedPlanet.getIncomingShipCount(john), `is`(3))
        assertThat(uninhabitedPlanet.getIncomingShipCount(jack), `is`(1))
    }

    @Test
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
    fun attackingWithSomeShipsReducesEnemyShips() {
        jacksHomePlanet.landShips(1, john)
        assertThat(jacksHomePlanet.getShipCount(), `is`(5))
        assertThat(jacksHomePlanet.inhabitant(), `is`(jack))

        jacksHomePlanet.landShips(2, john)
        assertThat(jacksHomePlanet.getShipCount(), `is`(3))
        assertThat(jacksHomePlanet.inhabitant(), `is`(jack))
    }

    @Test
    fun attackingWithEqualNumberOfShipsDestroysAllEnemyShips() {
        jacksHomePlanet.landShips(6, john)
        assertThat(jacksHomePlanet.getShipCount(), `is`(0))
        assertThat(jacksHomePlanet.inhabitant(), `is`(jack))
    }

    @Test
    fun attackingWithMoreShipsInhabitsEnemyPlanet() {
        jacksHomePlanet.landShips(7, john)
        assertThat(jacksHomePlanet.getShipCount(), `is`(1))
        assertThat(jacksHomePlanet.inhabitant(), `is`(john))
    }

    @Test
    fun invadingEnemyHomePlanetConvertsIntoRegularPlanet() {
        assertThat(jacksHomePlanet.isHomePlanet(), `is`(true))
        jacksHomePlanet.landShips(7, john)
        assertThat(jacksHomePlanet.isHomePlanet(), `is`(false))
    }

    @Test
    fun ownPlanetIsNotEnemyPlanet() {
        assertThat(johnsHomePlanet.isKnownAsEnemyPlanet(john), `is`(false))
    }

    @Test
    fun enemyPlanetIsNotRecognizedBeforeVisited() {
        assertThat(jacksHomePlanet.isKnownAsEnemyPlanet(john), `is`(false))
    }

    @Test
    fun enemyPlanetIsRecognizedAfterUnsuccessfulAttack() {
        jacksHomePlanet.landShips(1, john)
        assertThat(jacksHomePlanet.isKnownAsEnemyPlanet(john), `is`(true))
    }

    @Test
    fun enemyPlanetIsNoMoreEnemyAfterSuccessfulAttack() {
        jacksHomePlanet.landShips(1, john)
        jacksHomePlanet.landShips(7, john)
        assertThat(jacksHomePlanet.isKnownAsEnemyPlanet(john), `is`(false))
    }

    @Test
    fun successfullyInvadedPlanetBecomesEnemyPlanetForInvadedPlayer() {
        jacksPlanet.landShips(5, john)
        assertThat(jacksPlanet.inhabitant(), `is`(john))
        assertThat(jacksPlanet.isKnownAsEnemyPlanet(jack), `is`(true))
    }

    @Test
    fun invadingLostPlanetIsNotKnownAsEnemyPlanet() {
        jacksPlanet.landShips(1, john)
        jacksPlanet.handleDefeatedPlayer(jack)
        jacksPlanet.landShips(1, john)
        assertThat(jacksPlanet.isKnownAsEnemyPlanet(john), `is`(false))
    }

    @Test
    fun conqueringUninhabitedPlanetFiresEvent() {
        uninhabitedPlanet.landShips(1, john)
        verify(eventBus).fireConqueredUninhabitedPlanet(john)
        verifyNoMoreInteractions(eventBus)
    }

    @Test
    fun conqueringEnemyPlanetFiresEventsForBothPlayers() {
        jacksHomePlanet.landShips(20, john)
        verify(eventBus).fireConqueredEnemyPlanet(john)
        verify(eventBus).fireLostPlanet(jack)
        verifyNoMoreInteractions(eventBus)
    }

    @Test
    fun defendingPlanetFiresEventsForBothPlayers() {
        jacksHomePlanet.landShips(1, john)
        verify(eventBus).fireLostShipFormation(john)
        verify(eventBus).fireDefendedPlanet(jack)
        verifyNoMoreInteractions(eventBus)
    }

    @Test
    fun planetIsNotUnderAttackInitially() {
        assertThat(uninhabitedPlanet.isUnderAttack(), `is`(false))
    }

    @Test
    fun planetWasNotAttackedWhenItWasFirstInhabited() {
        uninhabitedPlanet.landShips(1, john)
        assertThat(uninhabitedPlanet.isUnderAttack(), `is`(false))
    }

    @Test
    fun planetWasNotAttackedWhenSuccessfullyInvaded() {
        uninhabitedPlanet.landShips(1, john)
        uninhabitedPlanet.landShips(2, jack)
        assertThat(uninhabitedPlanet.isUnderAttack(), `is`(false))
    }

    @Test
    fun planetWasAttackedWhenUnsuccessfullyInvaded() {
        uninhabitedPlanet.landShips(2, john)
        uninhabitedPlanet.landShips(1, jack)
        assertThat(uninhabitedPlanet.isUnderAttack(), `is`(true))
    }

    @Test
    fun planetIsNotUnderAttackAfterResetMarkers() {
        uninhabitedPlanet.landShips(2, john)
        uninhabitedPlanet.landShips(1, jack)
        uninhabitedPlanet.resetMarkers()
        assertThat(uninhabitedPlanet.isUnderAttack(), `is`(false))
    }

    @Test
    fun planetIsNotJustInhabitedInitially() {
        assertThat(uninhabitedPlanet.isJustInhabited(), `is`(false))
    }

    @Test
    fun planetIsJustInhabitedWhenItWasFirstInhabited() {
        uninhabitedPlanet.landShips(1, john)
        assertThat(uninhabitedPlanet.isJustInhabited(), `is`(true))
    }

    @Test
    fun planetIsJustInhabitedWhenSuccessfullyInvaded() {
        uninhabitedPlanet.landShips(1, john)
        uninhabitedPlanet.landShips(2, jack)
        assertThat(uninhabitedPlanet.isJustInhabited(), `is`(true))
    }

    @Test
    fun planetIsNotJustInhabitedWhenUnsuccessfullyInvaded() {
        uninhabitedPlanet.landShips(2, john)
        uninhabitedPlanet.resetMarkers()
        uninhabitedPlanet.landShips(1, jack)
        assertThat(uninhabitedPlanet.isJustInhabited(), `is`(false))
    }

    @Test
    fun planetIsNotJustInhabitedAfterResetMarkers() {
        uninhabitedPlanet.landShips(1, john)
        uninhabitedPlanet.resetMarkers()
        assertThat(uninhabitedPlanet.isJustInhabited(), `is`(false))
    }
}
