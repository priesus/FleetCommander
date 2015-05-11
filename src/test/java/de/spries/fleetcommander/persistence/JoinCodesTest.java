package de.spries.fleetcommander.persistence;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.management.*")
@PrepareForTest({ RandomStringUtils.class })
public class JoinCodesTest {

	private static final String CODE_REGEX = "[a-z0-9]{6}";

	@Before
	public void setUp() {
		JoinCodes.INSTANCE.reset();
	}

	@Test
	public void createsNonNullJoinCodes() throws Exception {
		assertThat(JoinCodes.INSTANCE.create(1), is(notNullValue()));
	}

	@Test
	public void codeAre6LowercaseAlphaNumericChars() throws Exception {
		String code = JoinCodes.INSTANCE.create(1);
		assertTrue("'" + code + "' should match RegEx '" + CODE_REGEX + "'", code.matches(CODE_REGEX));
	}

	@Test
	public void createsUniqueJoinCode() throws Exception {
		assertThat(JoinCodes.INSTANCE.create(1), is(not(JoinCodes.INSTANCE.create(1))));
	}

	@Test
	public void createsAnotherCodeIfNotUniqueOnFirstTry() throws Exception {
		PowerMockito.mockStatic(RandomStringUtils.class);
		PowerMockito.when(RandomStringUtils.randomAlphanumeric(6)).thenReturn("123456", "123456", "123456", "abcdef");

		assertThat(JoinCodes.INSTANCE.create(1), is(not(JoinCodes.INSTANCE.create(1))));
	}

	@Test
	public void invalidatingReturnsGameId() throws Exception {
		String code1 = JoinCodes.INSTANCE.create(1);
		String code2 = JoinCodes.INSTANCE.create(2);

		assertThat(JoinCodes.INSTANCE.invalidate(code1), is(1));
		assertThat(JoinCodes.INSTANCE.invalidate(code2), is(2));
	}

	@Test(expected = InvalidCodeException.class)
	public void canOnlyInvalidateOnce() throws Exception {
		String code1 = JoinCodes.INSTANCE.create(1);
		JoinCodes.INSTANCE.invalidate(code1);
		JoinCodes.INSTANCE.invalidate(code1);
	}

	@Test(expected = InvalidCodeException.class)
	public void cannotInvalidateInvalidCode() throws Exception {
		JoinCodes.INSTANCE.invalidate("InvalidCode");
	}

	@Test(expected = InvalidCodeException.class)
	public void cannotInvalidateNullCode() throws Exception {
		JoinCodes.INSTANCE.invalidate(null);
	}

	@Test
	public void invalidatesCaseInsensitive() throws Exception {
		PowerMockito.mockStatic(RandomStringUtils.class);
		PowerMockito.when(RandomStringUtils.randomAlphanumeric(6)).thenReturn("ABCde6");

		String code = JoinCodes.INSTANCE.create(1);

		assertThat(code, is("abcde6"));
		assertThat(JoinCodes.INSTANCE.invalidate("abcDE6"), is(1));
	}

	@Test
	public void canCreate5CodesPerGameMax() throws Exception {
		JoinCodes.INSTANCE.create(1);
		JoinCodes.INSTANCE.create(1);
		JoinCodes.INSTANCE.create(1);
		JoinCodes.INSTANCE.create(1);
		JoinCodes.INSTANCE.create(1);
		try {
			JoinCodes.INSTANCE.create(1);
			fail("Expected exception");
		} catch (JoinCodeLimitReachedException e) {
			// Expected behavior
		}
	}

	@Test
	public void canCreateMoreCodesAfterSomeHaveBeenInvalidated() throws Exception {
		JoinCodes.INSTANCE.create(1);
		String code1 = JoinCodes.INSTANCE.create(1);
		String code2 = JoinCodes.INSTANCE.create(1);
		String code3 = JoinCodes.INSTANCE.create(1);
		JoinCodes.INSTANCE.create(1);

		JoinCodes.INSTANCE.invalidate(code1);
		JoinCodes.INSTANCE.invalidate(code2);
		JoinCodes.INSTANCE.invalidate(code3);

		JoinCodes.INSTANCE.create(1);
		JoinCodes.INSTANCE.create(1);
		JoinCodes.INSTANCE.create(1);
		try {
			JoinCodes.INSTANCE.create(1);
			fail("Expected exception");
		} catch (JoinCodeLimitReachedException e) {
			// Expected behavior
		}
	}

	@Test
	public void returnsAllCodesForGame() throws Exception {
		String code1 = JoinCodes.INSTANCE.create(1);
		String code2 = JoinCodes.INSTANCE.create(1);
		String code3 = JoinCodes.INSTANCE.create(1);
		String code4 = JoinCodes.INSTANCE.create(1);
		String code5 = JoinCodes.INSTANCE.create(1);

		assertThat(JoinCodes.INSTANCE.get(1), hasItem(code1));
		assertThat(JoinCodes.INSTANCE.get(1), hasItem(code2));
		assertThat(JoinCodes.INSTANCE.get(1), hasItem(code3));
		assertThat(JoinCodes.INSTANCE.get(1), hasItem(code4));
		assertThat(JoinCodes.INSTANCE.get(1), hasItem(code5));
		assertThat(JoinCodes.INSTANCE.get(1), hasSize(5));
	}

	@Test
	public void doesntReturnInvalidatedCodes() throws Exception {
		JoinCodes.INSTANCE.create(1);
		String code1 = JoinCodes.INSTANCE.create(1);
		JoinCodes.INSTANCE.create(1);

		JoinCodes.INSTANCE.invalidate(code1);

		assertThat(JoinCodes.INSTANCE.get(1), not(hasItem(code1)));
	}

	@Test
	public void returnsCodesForGameIdOnly() throws Exception {
		String code1 = JoinCodes.INSTANCE.create(1);
		String code2 = JoinCodes.INSTANCE.create(2);

		assertThat(JoinCodes.INSTANCE.get(1), hasItem(code1));
		assertThat(JoinCodes.INSTANCE.get(1), hasSize(1));

		assertThat(JoinCodes.INSTANCE.get(2), hasItem(code2));
		assertThat(JoinCodes.INSTANCE.get(2), hasSize(1));
	}

	@Test
	public void returnsEmptyListForUnknownGameId() throws Exception {
		assertThat(JoinCodes.INSTANCE.get(100), is(empty()));
	}

	@Test
	public void invalidateAllRemovesAllCodesForGame() throws Exception {
		JoinCodes.INSTANCE.create(1);
		JoinCodes.INSTANCE.create(1);
		JoinCodes.INSTANCE.invalidateAll(1);

		assertThat(JoinCodes.INSTANCE.get(1), is(empty()));
	}

	@Test
	public void invalidateAllDoesntRemoveCodesFromOtherGame() throws Exception {
		JoinCodes.INSTANCE.create(1);
		JoinCodes.INSTANCE.create(2);
		JoinCodes.INSTANCE.invalidateAll(1);

		assertThat(JoinCodes.INSTANCE.get(2), hasSize(1));
	}

}
