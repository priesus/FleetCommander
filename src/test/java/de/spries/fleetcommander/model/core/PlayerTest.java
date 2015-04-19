package de.spries.fleetcommander.model.core;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import de.spries.fleetcommander.model.core.Player.InsufficientCreditsException;
import de.spries.fleetcommander.model.core.universe.FactorySite;

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

	@Test
	public void playerCanAffordAFactoryIfEnougnMoney() throws Exception {
		assertThat(john.canAffordFactory(), is(true));

		john.setCredits(FactorySite.FACTORY_COST);
		assertThat(john.canAffordFactory(), is(true));

		john.reduceCredits(1);
		assertThat(john.canAffordFactory(), is(false));
	}

	@Test
	public void stillEqualAfterCreditRecieval() {
		Player john2 = new Player("John");
		assertThat(john, is(john2));

		john2.addCredits(1);
		assertThat(john, is(john2));
	}

	@Test
	public void stillSameHashCodeAfterCreditRecieval() {
		Player john2 = new Player("John");
		assertThat(john.hashCode(), is(john2.hashCode()));

		john2.addCredits(1);
		assertThat(john.hashCode(), is(john2.hashCode()));
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
