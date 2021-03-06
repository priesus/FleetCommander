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
        assertThat(john.getName(), `is`("John"))
    }

    @Test
    fun isHumanPlayer() {
        assertThat(john.isHumanPlayer(), `is`(true))
    }

    @Test
    fun newPlayerHasNegativeId() {
        assertThat(john.getId(), `is`(lessThan(0)))
    }

    @Test
    fun newPlayerHasCredits() {
        assertThat(john.getCredits(), `is`(Player.STARTING_CREDITS))
    }

    @Test
    fun newPlayerIsPlaying() {
        assertThat(john.getStatus(), `is`(Status.PLAYING))
        assertThat(john.isActive(), `is`(true))
        assertThat(john.isReady(), `is`(false))
    }

    @Test
    fun readyPlayerIsReady() {
        john.setReady()
        assertThat(john.getStatus(), `is`(Status.READY))
        assertThat(john.isActive(), `is`(true))
        assertThat(john.isReady(), `is`(true))
    }

    @Test(expected = IllegalActionException::class)
    fun cannotSetReadyTwice() {
        john.setReady()
        john.setReady()
    }

    @Test
    fun readyPlayerIschangedToPlaying() {
        john.setReady()
        john.setPlaying()
        assertThat(john.getStatus(), `is`(Status.PLAYING))
        assertThat(john.isActive(), `is`(true))
        assertThat(john.isReady(), `is`(false))
    }

    @Test(expected = IllegalActionException::class)
    fun cannotSetPlayingForDefeatedPlayer() {
        john.handleDefeat()
        john.setPlaying()
    }

    @Test(expected = IllegalActionException::class)
    fun cannotSetPlayingForQuitPlayer() {
        john.handleQuit()
        john.setPlaying()
    }

    @Test
    fun defeatedPlayerIsDefeated() {
        john.handleDefeat()
        assertThat(john.getStatus(), `is`(Status.DEFEATED))
        assertThat(john.isActive(), `is`(false))
    }

    @Test
    fun quittingPlayerIsntActiveAnymore() {
        john.handleQuit()
        assertThat(john.getStatus(), `is`(Status.QUIT))
        assertThat(john.isActive(), `is`(false))
    }

    @Test(expected = IllegalActionException::class)
    fun cannotQuitTwice() {
        john.handleQuit()
        john.handleQuit()
    }

    @Test
    fun playerCanAffordAFactoryIfEnoughMoney() {
        assertThat(john.canAffordFactory(), `is`(true))

        john.setCredits(FactorySite.FACTORY_COST)
        assertThat(john.canAffordFactory(), `is`(true))

        john.reduceCredits(1)
        assertThat(john.canAffordFactory(), `is`(false))
    }

    @Test
    fun playersAreStillEqualWithDifferentId() {
        val john2 = Player("John")
        john.assignId(200)
        john2.assignId(200)
        assertThat(john, `is`(john2))

        john2.assignId(201)
        assertThat(john, `is`(john2))
    }

    @Test
    fun playersHaveSameHashCodeEqualWithDifferentId() {
        val john2 = Player("John")
        john.assignId(200)
        john2.assignId(200)
        assertThat(john, `is`(john2))
        assertThat(john.hashCode(), `is`(john2.hashCode()))

        john2.assignId(201)
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
    fun playerCannotBuyStuffHeCannotAfford() {
        john.reduceCredits(Player.STARTING_CREDITS + 1)
    }

    @Test
    fun buyingStuffReducesPlayersCredits() {
        john.reduceCredits(HALF_STARTING_BALANCE)
        assertThat(john.getCredits(), `is`(HALF_STARTING_BALANCE))

        john.reduceCredits(HALF_STARTING_BALANCE)
        assertThat(john.getCredits(), `is`(0))
    }

    @Test
    fun addingCreditsIncreasesBalance() {
        john.addCredits(1)
        assertThat(john.getCredits(), `is`(Player.STARTING_CREDITS + 1))

        john.addCredits(10)
        assertThat(john.getCredits(), `is`(Player.STARTING_CREDITS + 11))
    }

    @Test
    fun playerCretidsAreCapped() {
        john.addCredits(Player.MAX_CREDITS)
        assertThat(john.getCredits(), `is`(Player.MAX_CREDITS))
    }

    companion object {

        private const val HALF_STARTING_BALANCE = Player.STARTING_CREDITS / 2
    }

}
