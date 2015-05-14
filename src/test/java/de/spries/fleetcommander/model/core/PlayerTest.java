package de.spries.fleetcommander.model.core;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
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
	public void newPlayerHasName() {
		assertThat(john.getName(), is("John"));
	}

	@Test
	public void isHumanPlayer() throws Exception {
		assertThat(john.isHumanPlayer(), is(true));
	}

	@Test
	public void newPlayerHasNegativeId() throws Exception {
		assertThat(john.getId(), is(lessThan(0)));
	}

	@Test
	public void newPlayerHasCredits() {
		assertThat(john.getCredits(), is(Player.STARTING_CREDITS));
	}

	@Test
	public void newPlayerIsActive() {
		assertThat(john.isActive(), is(true));
	}

	@Test
	public void newPlayerHasntQuitYet() {
		assertThat(john.hasQuit(), is(false));
	}

	@Test
	public void defeatedPlayerIsNotActiveAnymore() throws Exception {
		john.handleDefeat();
		assertThat(john.isActive(), is(false));
	}

	@Test
	public void quittingPlayerHasQuit() throws Exception {
		john.handleQuit();
		assertThat(john.hasQuit(), is(true));
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
	public void playersAreEqualWithSameId() throws Exception {
		Player john2 = new Player("John");
		john.setId(200);
		john2.setId(200);
		assertThat(john, is(john2));

		john2.setId(201);
		assertThat(john, is(not(john2)));
	}

	@Test
	public void playersHaveSameHashCodeEqualWithSameId() throws Exception {
		Player john2 = new Player("John");
		john.setId(200);
		john2.setId(200);
		assertThat(john, is(john2));
		assertThat(john.hashCode(), is(john2.hashCode()));

		john2.setId(201);
		assertThat(john.hashCode(), is(not(john2.hashCode())));
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
