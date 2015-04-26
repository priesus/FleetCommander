package de.spries.fleetcommander.model.ai;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.spries.fleetcommander.model.core.Game;
import de.spries.fleetcommander.model.core.Player;
import de.spries.fleetcommander.model.facade.PlayerSpecificGame;
import de.spries.fleetcommander.model.facade.PlayerSpecificUniverse;

public class ComputerPlayer extends Player {

	private static final Logger LOGGER = LogManager.getLogger(ComputerPlayer.class.getName());
	private BuildingStrategy buildingStrategy;
	private FleetStrategy fleetStrategy;

	public ComputerPlayer(String name, BuildingStrategy buildingStrategy, FleetStrategy fleetStrategy) {
		super(name);
		this.buildingStrategy = buildingStrategy;
		this.fleetStrategy = fleetStrategy;
	}

	@Override
	public void notifyNewTurn(Game game) {
		try {
			playTurn(new PlayerSpecificGame(game, this));
		} catch (Exception e) {
			// Just end the turn (still it shouldn't happen)
			String msg = String.format("Game %d: Computer player '%s' caused an exception: ", game.getId(), getName());
			LOGGER.warn(msg, e);
		}
		game.endTurn(this);
	}

	public void playTurn(PlayerSpecificGame game) {
		PlayerSpecificUniverse universe = game.getUniverse();
		buildingStrategy.buildFactories(universe);
		fleetStrategy.sendShips(universe);
	}

}
