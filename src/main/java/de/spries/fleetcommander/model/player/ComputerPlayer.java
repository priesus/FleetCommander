package de.spries.fleetcommander.model.player;

import de.spries.fleetcommander.model.Game;

public class ComputerPlayer extends Player {

	public ComputerPlayer(String name) {
		super(name);
	}

	@Override
	public void notifyNewTurn(Game game) {
		super.notifyNewTurn(game);
		game.endTurn(this);
	}

}
