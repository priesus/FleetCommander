package de.spries.fleetcommander.persistence

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.empty
import org.hamcrest.Matchers.hasItem
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.not
import org.junit.Assert.assertThat
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class JoinCodesTest {

    private val joinCodeRepo = JoinCodeRepository()

    @Before
    fun setUp() {
        joinCodeRepo.reset()
    }

    @Test
    fun createsAnotherCodeIfNotUniqueOnFirstTry() {
        val randomGenerator = mock<() -> String>()
        val jcr = JoinCodeRepository(randomGenerator)
        whenever(randomGenerator.invoke()).thenReturn("123456", "123456", "123456", "abcdef")

        assertThat(jcr.create(1), `is`(not(jcr.create(1))))
    }

    @Test
    fun invalidatingReturnsGameId() {
        val code1 = joinCodeRepo.create(1)
        val code2 = joinCodeRepo.create(2)

        assertThat(joinCodeRepo.invalidate(code1), `is`(1))
        assertThat(joinCodeRepo.invalidate(code2), `is`(2))
    }

    fun canOnlyInvalidateOnce() {
        val code1 = joinCodeRepo.create(1)
        joinCodeRepo.invalidate(code1)
        joinCodeRepo.invalidate(code1)
    }

    fun cannotInvalidateInvalidCode() {
        joinCodeRepo.invalidate("InvalidCode")
    }

    fun cannotInvalidateNullCode() {
        joinCodeRepo.invalidate(null)
    }

    @Test
    fun invalidatesCaseInsensitive() {
        val randomGenerator = mock<() -> String>()
        val jcr = JoinCodeRepository(randomGenerator)
        whenever(randomGenerator.invoke()).thenReturn("abcde6")

        val code = jcr.create(1)

        assertThat(jcr.invalidate("abcDE6"), `is`(1))
    }

    @Test
    fun canCreate5CodesPerGameMax() {
        joinCodeRepo.create(1)
        joinCodeRepo.create(1)
        joinCodeRepo.create(1)
        joinCodeRepo.create(1)
        joinCodeRepo.create(1)
        try {
            joinCodeRepo.create(1)
            fail("Expected exception")
        } catch (e: JoinCodeRepository.JoinCodeLimitReachedException) {
            // Expected behavior
        }

    }

    @Test
    fun canCreateMoreCodesAfterSomeHaveBeenInvalidated() {
        joinCodeRepo.create(1)
        val code1 = joinCodeRepo.create(1)
        val code2 = joinCodeRepo.create(1)
        val code3 = joinCodeRepo.create(1)
        joinCodeRepo.create(1)

        joinCodeRepo.invalidate(code1)
        joinCodeRepo.invalidate(code2)
        joinCodeRepo.invalidate(code3)

        joinCodeRepo.create(1)
        joinCodeRepo.create(1)
        joinCodeRepo.create(1)
        try {
            joinCodeRepo.create(1)
            fail("Expected exception")
        } catch (e: JoinCodeRepository.JoinCodeLimitReachedException) {
            // Expected behavior
        }

    }

    @Test
    fun returnsAllCodesForGame() {
        val code1 = joinCodeRepo.create(1)
        val code2 = joinCodeRepo.create(1)
        val code3 = joinCodeRepo.create(1)
        val code4 = joinCodeRepo.create(1)
        val code5 = joinCodeRepo.create(1)

        assertThat(joinCodeRepo[1], hasItem(code1))
        assertThat(joinCodeRepo[1], hasItem(code2))
        assertThat(joinCodeRepo[1], hasItem(code3))
        assertThat(joinCodeRepo[1], hasItem(code4))
        assertThat(joinCodeRepo[1], hasItem(code5))
        assertThat(joinCodeRepo[1], hasSize(5))
    }

    @Test
    fun doesntReturnInvalidatedCodes() {
        joinCodeRepo.create(1)
        val code1 = joinCodeRepo.create(1)
        joinCodeRepo.create(1)

        joinCodeRepo.invalidate(code1)

        assertThat(joinCodeRepo[1], not(hasItem(code1)))
    }

    @Test
    fun returnsCodesForGameIdOnly() {
        val code1 = joinCodeRepo.create(1)
        val code2 = joinCodeRepo.create(2)

        assertThat(joinCodeRepo[1], hasItem(code1))
        assertThat(joinCodeRepo[1], hasSize(1))

        assertThat(joinCodeRepo[2], hasItem(code2))
        assertThat(joinCodeRepo[2], hasSize(1))
    }

    @Test
    fun returnsEmptyListForUnknownGameId() {
        assertThat(joinCodeRepo[100], `is`(empty()))
    }

    @Test
    fun invalidateAllRemovesAllCodesForGame() {
        joinCodeRepo.create(1)
        joinCodeRepo.create(1)
        joinCodeRepo.invalidateAll(1)

        assertThat(joinCodeRepo[1], `is`(empty()))
    }

    @Test
    fun invalidateAllDoesntRemoveCodesFromOtherGame() {
        joinCodeRepo.create(1)
        joinCodeRepo.create(2)
        joinCodeRepo.invalidateAll(1)

        assertThat(joinCodeRepo[2], hasSize(1))
    }
}
