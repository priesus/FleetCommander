package de.spries.fleetcommander;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import de.spries.fleetcommander.Player.InsufficientCreditsException;

public class PlayerTest {

	private static final int HALF_STARTING_BALANCE = Player.STARTING_BALANCE / 2;
	private Player john;

	@Before
	public void setUp() {
		john = new Player("John");
	}

	@Test
	public void playerHasNameAndCredits() {
		assertThat(john.getName(), is("John"));
		assertThat(john.getCredits(), is(Player.STARTING_BALANCE));
	}

	@Test(expected = InsufficientCreditsException.class)
	public void playerCannotBuyStuffHeCannotAfford() throws Exception {
		john.reduceCredits(Player.STARTING_BALANCE + 1);
	}

	@Test
	public void buyingStuffReducesPlayersCredits() throws Exception {
		john.reduceCredits(HALF_STARTING_BALANCE);
		assertThat(john.getCredits(), is(HALF_STARTING_BALANCE));

		john.reduceCredits(HALF_STARTING_BALANCE);
		assertThat(john.getCredits(), is(0));
	}

}
