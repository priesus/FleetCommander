package de.spries.fleetcommander.model.core

import de.spries.fleetcommander.model.core.Player.InsufficientCreditsException
import de.spries.fleetcommander.model.core.Player.Status
import de.spries.fleetcommander.model.core.common.IllegalActionException
import de.spries.fleetcommander.model.core.universe.FactorySite
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.lessThan
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test

class PlayerTest {
    private lateinit var john: Player

    @Before
    fun setUp() {
        john = Player("John")
    }

    @Test
    fun newPlayerHasName() {
        assertThat(john.name, `is`("John"))
    }

    @Test
    @Throws(Exception::class)
    fun isHumanPlayer() {
        assertThat(john.isHumanPlayer(), `is`(true))
    }

    @Test
    @Throws(Exception::class)
    fun newPlayerHasNegativeId() {
        assertThat(john.id, `is`(lessThan(0)))
    }

    @Test
    fun newPlayerHasCredits() {
        assertThat(john.credits, `is`(Player.STARTING_CREDITS))
    }

    @Test
    fun newPlayerIsPlaying() {
        assertThat(john.status, `is`(Status.PLAYING))
        assertThat(john.isActive(), `is`(true))
        assertThat(john.isReady(), `is`(false))
    }

    @Test
    @Throws(Exception::class)
    fun readyPlayerIsReady() {
        john.setReady()
        assertThat(john.status, `is`(Status.READY))
        assertThat(john.isActive(), `is`(true))
        assertThat(john.isReady(), `is`(true))
    }

    @Test(expected = IllegalActionException::class)
    @Throws(Exception::class)
    fun cannotSetReadyTwice() {
        john.setReady()
        john.setReady()
    }

    @Test
    @Throws(Exception::class)
    fun readyPlayerIschangedToPlaying() {
        john.setReady()
        john.setPlaying()
        assertThat(john.status, `is`(Status.PLAYING))
        assertThat(john.isActive(), `is`(true))
        assertThat(john.isReady(), `is`(false))
    }

    @Test(expected = IllegalActionException::class)
    @Throws(Exception::class)
    fun cannotSetPlayingForDefeatedPlayer() {
        john.handleDefeat()
        john.setPlaying()
    }

    @Test(expected = IllegalActionException::class)
    @Throws(Exception::class)
    fun cannotSetPlayingForQuitPlayer() {
        john.handleQuit()
        john.setPlaying()
    }

    @Test
    @Throws(Exception::class)
    fun defeatedPlayerIsDefeated() {
        john.handleDefeat()
        assertThat(john.status, `is`(Status.DEFEATED))
        assertThat(john.isActive(), `is`(false))
    }

    @Test
    @Throws(Exception::class)
    fun quittingPlayerIsntActiveAnymore() {
        john.handleQuit()
        assertThat(john.status, `is`(Status.QUIT))
        assertThat(john.isActive(), `is`(false))
    }

    @Test(expected = IllegalActionException::class)
    @Throws(Exception::class)
    fun cannotQuitTwice() {
        john.handleQuit()
        john.handleQuit()
    }

    @Test
    @Throws(Exception::class)
    fun playerCanAffordAFactoryIfEnougnMoney() {
        assertThat(john.canAffordFactory(), `is`(true))

        john.credits = FactorySite.FACTORY_COST
        assertThat(john.canAffordFactory(), `is`(true))

        john.reduceCredits(1)
        assertThat(john.canAffordFactory(), `is`(false))
    }

    @Test
    @Throws(Exception::class)
    fun playersAreStillEqualWithDifferentId() {
        val john2 = Player("John")
        john.id = 200
        john2.id = 200
        assertThat(john, `is`(john2))

        john2.id = 201
        assertThat(john, `is`(john2))
    }

    @Test
    @Throws(Exception::class)
    fun playersHaveSameHashCodeEqualWithDifferentId() {
        val john2 = Player("John")
        john.id = 200
        john2.id = 200
        assertThat(john, `is`(john2))
        assertThat(john.hashCode(), `is`(john2.hashCode()))

        john2.id = 201
        assertThat(john.hashCode(), `is`(john2.hashCode()))
    }

    @Test
    fun stillEqualAfterCreditRecieval() {
        val john2 = Player("John")
        assertThat(john, `is`(john2))

        john2.addCredits(1)
        assertThat(john, `is`(john2))
    }

    @Test
    fun stillSameHashCodeAfterCreditRecieval() {
        val john2 = Player("John")
        assertThat(john.hashCode(), `is`(john2.hashCode()))

        john2.addCredits(1)
        assertThat(john.hashCode(), `is`(john2.hashCode()))
    }

    @Test(expected = InsufficientCreditsException::class)
    @Throws(Exception::class)
    fun playerCannotBuyStuffHeCannotAfford() {
        john.reduceCredits(Player.STARTING_CREDITS + 1)
    }

    @Test
    @Throws(Exception::class)
    fun buyingStuffReducesPlayersCredits() {
        john.reduceCredits(HALF_STARTING_BALANCE)
        assertThat(john.credits, `is`(HALF_STARTING_BALANCE))

        john.reduceCredits(HALF_STARTING_BALANCE)
        assertThat(john.credits, `is`(0))
    }

    @Test
    @Throws(Exception::class)
    fun addingCreditsIncreasesBalance() {
        john.addCredits(1)
        assertThat(john.credits, `is`(Player.STARTING_CREDITS + 1))

        john.addCredits(10)
        assertThat(john.credits, `is`(Player.STARTING_CREDITS + 11))
    }

    @Test
    @Throws(Exception::class)
    fun playerCretidsAreCapped() {
        john.addCredits(Player.MAX_CREDITS)
        assertThat(john.credits, `is`(Player.MAX_CREDITS))
    }

    companion object {

        private val HALF_STARTING_BALANCE = Player.STARTING_CREDITS / 2
    }

}
