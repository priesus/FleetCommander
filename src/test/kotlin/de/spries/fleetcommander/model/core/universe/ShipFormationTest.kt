package de.spries.fleetcommander.model.core.universe

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import de.spries.fleetcommander.model.core.Player
import de.spries.fleetcommander.model.core.common.IllegalActionException
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.greaterThan
import org.hamcrest.Matchers.lessThan
import org.hamcrest.Matchers.lessThanOrEqualTo
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.never
import org.mockito.Mockito.verify

class ShipFormationTest {
    private lateinit var existingFormation: ShipFormation
    private lateinit var originPlanet: Planet
    private lateinit var closePlanet: Planet
    private lateinit var moreDistantPlanet: Planet
    private lateinit var distantPlanet: Planet

    @Before
    fun setUp() {
        originPlanet = mock()
        closePlanet = mock()
        moreDistantPlanet = mock()
        distantPlanet = mock()

        existingFormation = ShipFormation(1, originPlanet, closePlanet, JOHN)
        whenever(originPlanet.distanceTo(closePlanet)).thenReturn(1.0 * ShipFormation.DISTANCE_PER_TURN)
        whenever(originPlanet.distanceTo(moreDistantPlanet)).thenReturn(2.0 * ShipFormation.DISTANCE_PER_TURN)
        whenever(originPlanet.distanceTo(distantPlanet)).thenReturn(3.0 * ShipFormation.DISTANCE_PER_TURN)
    }

    @Test(expected = IllegalActionException::class)
    fun shipCountMustBeNonNegative() {
        ShipFormation(-1, originPlanet, originPlanet, JOHN)
    }

    @Test(expected = IllegalActionException::class)
    fun shipCountMustBeNonZero() {
        ShipFormation(0, originPlanet, originPlanet, JOHN)
    }

    @Test
    fun formationCannotJoinIfOriginIsDifferent() {
        val newShipFormation = ShipFormation(1, closePlanet, closePlanet, JOHN)
        assertThat(newShipFormation.canJoin(existingFormation), `is`(false))
    }

    @Test
    fun formationCannotJoinIfDestinationIsDifferent() {
        val newShipFormation = ShipFormation(1, originPlanet, originPlanet, JOHN)
        assertThat(newShipFormation.canJoin(existingFormation), `is`(false))
    }

    @Test
    fun formationCannotJoinIfCommanderIsDifferent() {
        val newShipFormation = ShipFormation(1, originPlanet, closePlanet, JACK)
        assertThat(newShipFormation.canJoin(existingFormation), `is`(false))
    }

    @Test
    fun formationCannotJoinIfDistancetravelledIsDifferent() {
        val newShipFormation = ShipFormation(1, originPlanet, closePlanet, JOHN)
        newShipFormation.travel()
        assertThat(newShipFormation.canJoin(existingFormation), `is`(false))
    }

    @Test
    fun formationCanJoinIfRouteAndCommanderAreEqual() {
        val newShipFormation = ShipFormation(1, originPlanet, closePlanet, JOHN)
        assertThat(newShipFormation.canJoin(existingFormation), `is`(true))
    }

    @Test
    fun mergingIncreasesShipCountOfExistingFormation() {
        val joiningFormation = ShipFormation(1, originPlanet, closePlanet, JOHN)
        joiningFormation.join(existingFormation)
        assertThat(existingFormation.getShipCount(), `is`(2))
    }

    @Test
    fun mergingDecreasesShipCountOfJoiningFormationToZero() {
        val joiningFormation = ShipFormation(1, originPlanet, closePlanet, JOHN)
        joiningFormation.join(existingFormation)
        assertThat(joiningFormation.getShipCount(), `is`(0))
    }

    @Test(expected = IllegalArgumentException::class)
    fun cannotMergeFormationsIfCommanderIsNotEqual() {
        val joiningFormation = ShipFormation(1, originPlanet, closePlanet, JACK)
        joiningFormation.join(existingFormation)
    }

    @Test
    fun landingOnDestinationTransfersShipsToPlanet() {
        existingFormation.landOnDestination()
        assertThat(existingFormation.getShipCount(), `is`(0))
        verify(closePlanet).landShips(1, JOHN)
    }

    @Test
    @Throws(Exception::class)
    fun shipsHaventArrivedBeforeTravelling() {
        val sf = ShipFormation(1, originPlanet, closePlanet, JOHN)
        assertThat(sf.hasArrived(), `is`(false))
    }

    @Test
    @Throws(Exception::class)
    fun shortTripLandsAfter1Cycle() {
        val sf = ShipFormation(1, originPlanet, closePlanet, JOHN)
        sf.travel()
        verify(closePlanet).landShips(1, JOHN)
        assertThat(sf.hasArrived(), `is`(true))
    }

    @Test
    @Throws(Exception::class)
    fun longerTripEndsAfter2Cycles() {
        val sf = ShipFormation(1, originPlanet, moreDistantPlanet, JOHN)

        sf.travel()
        verify(moreDistantPlanet, never()).landShips(1, JOHN)
        assertThat(sf.hasArrived(), `is`(false))

        sf.travel()
        verify(moreDistantPlanet).landShips(1, JOHN)
        assertThat(sf.hasArrived(), `is`(true))
    }

    @Test
    @Throws(Exception::class)
    fun longTripEndsAfter3Cycles() {
        val sf = ShipFormation(1, originPlanet, distantPlanet, JOHN)

        sf.travel()
        verify(distantPlanet, never()).landShips(1, JOHN)

        sf.travel()
        verify(distantPlanet, never()).landShips(1, JOHN)
        assertThat(sf.hasArrived(), `is`(false))
        assertThat(sf.getDistanceRemaining(), `is`(greaterThan(0.0)))

        sf.travel()
        verify(distantPlanet).landShips(1, JOHN)
        assertThat(sf.hasArrived(), `is`(true))
        assertThat(sf.getDistanceRemaining(), `is`(lessThanOrEqualTo(0.0)))
    }

    @Test
    @Throws(Exception::class)
    fun distanceTravelledIncreasesWithEachCycle() {
        val sf = ShipFormation(1, originPlanet, moreDistantPlanet, JOHN)

        sf.travel()
        assertThat(sf.getDistanceTravelled(), `is`(1 * ShipFormation.DISTANCE_PER_TURN))
        sf.travel()
        assertThat(sf.getDistanceTravelled(), `is`(2 * ShipFormation.DISTANCE_PER_TURN))
    }

    @Test
    @Throws(Exception::class)
    fun formationPositionMovesTowardsDestination() {
        doReturn(0).`when`(originPlanet).x
        doReturn(0).`when`(originPlanet).y
        doReturn(15).`when`(distantPlanet).x
        doReturn(15).`when`(distantPlanet).y

        val sf = ShipFormation(1, originPlanet, distantPlanet, JOHN)
        assertThat(sf.x, `is`(0))
        assertThat(sf.y, `is`(0))

        sf.travel()
        assertThat(sf.x, `is`(greaterThan(0)))
        assertThat(sf.y, `is`(greaterThan(0)))
        assertThat(sf.x, `is`(lessThan(15)))
        assertThat(sf.y, `is`(lessThan(15)))
    }

    companion object {

        private val JOHN = mock<Player>()
        private val JACK = mock<Player>()
    }

}
