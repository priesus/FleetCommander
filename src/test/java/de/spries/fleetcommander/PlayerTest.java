package de.spries.fleetcommander;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class PlayerTest {

	@Test
	public void playerHasNameAndCredits() {
		Player player = new Player("John");
		assertThat(player.getName(), is("John"));
		assertThat(player.getCredits(), is(500));
	}

}
