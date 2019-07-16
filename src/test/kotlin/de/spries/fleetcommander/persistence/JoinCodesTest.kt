package de.spries.fleetcommander.persistence

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.empty
import org.hamcrest.Matchers.hasItem
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.notNullValue
import org.junit.Assert.assertThat
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import java.util.concurrent.ThreadLocalRandom
import kotlin.streams.asSequence

class JoinCodesTest {

    @Before
    fun setUp() {
        JoinCodes.INSTANCE.reset()
        JoinCodes.INSTANCE.randomGenerator = ({
            val charPool: List<Char> = ('a'..'z') + ('0'..'9')
            ThreadLocalRandom.current()
                    .ints(6, 0, charPool.size)
                    .asSequence()
                    .map(charPool::get)
                    .joinToString("")
        })
    }

    @Test
    @Throws(Exception::class)
    fun createsAnotherCodeIfNotUniqueOnFirstTry() {
        JoinCodes.INSTANCE.randomGenerator = mock()
        whenever(JoinCodes.INSTANCE.randomGenerator.invoke()).thenReturn("123456", "123456", "123456", "abcdef")

        assertThat(JoinCodes.INSTANCE.create(1), `is`(not(JoinCodes.INSTANCE.create(1))))
    }

    @Test
    @Throws(Exception::class)
    fun invalidatingReturnsGameId() {
        val code1 = JoinCodes.INSTANCE.create(1)
        val code2 = JoinCodes.INSTANCE.create(2)

        assertThat(JoinCodes.INSTANCE.invalidate(code1), `is`(1))
        assertThat(JoinCodes.INSTANCE.invalidate(code2), `is`(2))
    }

    @Test(expected = InvalidCodeException::class)
    @Throws(Exception::class)
    fun canOnlyInvalidateOnce() {
        val code1 = JoinCodes.INSTANCE.create(1)
        JoinCodes.INSTANCE.invalidate(code1)
        JoinCodes.INSTANCE.invalidate(code1)
    }

    @Test(expected = InvalidCodeException::class)
    @Throws(Exception::class)
    fun cannotInvalidateInvalidCode() {
        JoinCodes.INSTANCE.invalidate("InvalidCode")
    }

    @Test(expected = InvalidCodeException::class)
    @Throws(Exception::class)
    fun cannotInvalidateNullCode() {
        JoinCodes.INSTANCE.invalidate(null)
    }

    @Test
    @Throws(Exception::class)
    fun invalidatesCaseInsensitive() {
        JoinCodes.INSTANCE.randomGenerator = mock()
        whenever(JoinCodes.INSTANCE.randomGenerator.invoke()).thenReturn("abcde6")

        val code = JoinCodes.INSTANCE.create(1)

        assertThat(JoinCodes.INSTANCE.invalidate("abcDE6"), `is`(1))
    }

    @Test
    @Throws(Exception::class)
    fun canCreate5CodesPerGameMax() {
        JoinCodes.INSTANCE.create(1)
        JoinCodes.INSTANCE.create(1)
        JoinCodes.INSTANCE.create(1)
        JoinCodes.INSTANCE.create(1)
        JoinCodes.INSTANCE.create(1)
        try {
            JoinCodes.INSTANCE.create(1)
            fail("Expected exception")
        } catch (e: JoinCodeLimitReachedException) {
            // Expected behavior
        }

    }

    @Test
    @Throws(Exception::class)
    fun canCreateMoreCodesAfterSomeHaveBeenInvalidated() {
        JoinCodes.INSTANCE.create(1)
        val code1 = JoinCodes.INSTANCE.create(1)
        val code2 = JoinCodes.INSTANCE.create(1)
        val code3 = JoinCodes.INSTANCE.create(1)
        JoinCodes.INSTANCE.create(1)

        JoinCodes.INSTANCE.invalidate(code1)
        JoinCodes.INSTANCE.invalidate(code2)
        JoinCodes.INSTANCE.invalidate(code3)

        JoinCodes.INSTANCE.create(1)
        JoinCodes.INSTANCE.create(1)
        JoinCodes.INSTANCE.create(1)
        try {
            JoinCodes.INSTANCE.create(1)
            fail("Expected exception")
        } catch (e: JoinCodeLimitReachedException) {
            // Expected behavior
        }

    }

    @Test
    @Throws(Exception::class)
    fun returnsAllCodesForGame() {
        val code1 = JoinCodes.INSTANCE.create(1)
        val code2 = JoinCodes.INSTANCE.create(1)
        val code3 = JoinCodes.INSTANCE.create(1)
        val code4 = JoinCodes.INSTANCE.create(1)
        val code5 = JoinCodes.INSTANCE.create(1)

        assertThat(JoinCodes.INSTANCE[1], hasItem(code1))
        assertThat(JoinCodes.INSTANCE[1], hasItem(code2))
        assertThat(JoinCodes.INSTANCE[1], hasItem(code3))
        assertThat(JoinCodes.INSTANCE[1], hasItem(code4))
        assertThat(JoinCodes.INSTANCE[1], hasItem(code5))
        assertThat(JoinCodes.INSTANCE[1], hasSize(5))
    }

    @Test
    @Throws(Exception::class)
    fun doesntReturnInvalidatedCodes() {
        JoinCodes.INSTANCE.create(1)
        val code1 = JoinCodes.INSTANCE.create(1)
        JoinCodes.INSTANCE.create(1)

        JoinCodes.INSTANCE.invalidate(code1)

        assertThat(JoinCodes.INSTANCE[1], not(hasItem(code1)))
    }

    @Test
    @Throws(Exception::class)
    fun returnsCodesForGameIdOnly() {
        val code1 = JoinCodes.INSTANCE.create(1)
        val code2 = JoinCodes.INSTANCE.create(2)

        assertThat(JoinCodes.INSTANCE[1], hasItem(code1))
        assertThat(JoinCodes.INSTANCE[1], hasSize(1))

        assertThat(JoinCodes.INSTANCE[2], hasItem(code2))
        assertThat(JoinCodes.INSTANCE[2], hasSize(1))
    }

    @Test
    @Throws(Exception::class)
    fun returnsEmptyListForUnknownGameId() {
        assertThat(JoinCodes.INSTANCE[100], `is`(empty()))
    }

    @Test
    @Throws(Exception::class)
    fun invalidateAllRemovesAllCodesForGame() {
        JoinCodes.INSTANCE.create(1)
        JoinCodes.INSTANCE.create(1)
        JoinCodes.INSTANCE.invalidateAll(1)

        assertThat(JoinCodes.INSTANCE[1], `is`(empty()))
    }

    @Test
    @Throws(Exception::class)
    fun invalidateAllDoesntRemoveCodesFromOtherGame() {
        JoinCodes.INSTANCE.create(1)
        JoinCodes.INSTANCE.create(2)
        JoinCodes.INSTANCE.invalidateAll(1)

        assertThat(JoinCodes.INSTANCE[2], hasSize(1))
    }
}
