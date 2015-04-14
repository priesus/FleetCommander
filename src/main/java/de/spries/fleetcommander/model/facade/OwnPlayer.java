package de.spries.fleetcommander.model.facade;

import de.spries.fleetcommander.model.core.Player;

public class OwnPlayer extends OtherPlayer {

	public OwnPlayer(Player originalPlayer) {
		super(originalPlayer);
	}

	public Integer getCredits() {
		return originalPlayer.getCredits();
	}
}
