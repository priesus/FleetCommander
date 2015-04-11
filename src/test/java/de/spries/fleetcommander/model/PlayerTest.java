package de.spries.fleetcommander.model;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import de.spries.fleetcommander.model.Player;
import de.spries.fleetcommander.model.Player.InsufficientCreditsException;

public class PlayerTest {

	private static final int HALF_STARTING_BALANCE = Player.STARTING_CREDITS / 2;
	private Player john;

	@Before
	public void setUp() {
		john = new Player("John");
	}

	@Test
	public void playerHasNameAndCredits() {
		assertThat(john.getName(), is("John"));
		assertThat(john.getCredits(), is(Player.STARTING_CREDITS));
	}

	@Test(expected = InsufficientCreditsException.class)
	public void playerCannotBuyStuffHeCannotAfford() throws Exception {
		john.reduceCredits(Player.STARTING_CREDITS + 1);
	}

	@Test
	public void buyingStuffReducesPlayersCredits() throws Exception {
		john.reduceCredits(HALF_STARTING_BALANCE);
		assertThat(john.getCredits(), is(HALF_STARTING_BALANCE));

		john.reduceCredits(HALF_STARTING_BALANCE);
		assertThat(john.getCredits(), is(0));
	}

	@Test
	public void addingCreditsIncreasesBalance() throws Exception {
		john.addCredits(1);
		assertThat(john.getCredits(), is(Player.STARTING_CREDITS + 1));

		john.addCredits(10);
		assertThat(john.getCredits(), is(Player.STARTING_CREDITS + 11));
	}

	@Test
	public void playerCretidsAreCapped() throws Exception {
		john.addCredits(Player.MAX_CREDITS);
		assertThat(john.getCredits(), is(Player.MAX_CREDITS));
	}

}
