package de.spries.fleetcommander.model.facade;

import java.util.List;

import de.spries.fleetcommander.model.ai.AggressiveFleetStrategy;
import de.spries.fleetcommander.model.ai.ComputerPlayer;
import de.spries.fleetcommander.model.ai.DefaultBuildingStrategy;
import de.spries.fleetcommander.model.core.Game;
import de.spries.fleetcommander.model.core.Game.GameStatus;
import de.spries.fleetcommander.model.core.Player;
import de.spries.fleetcommander.model.core.TurnEvents;
import de.spries.fleetcommander.model.core.universe.UniverseFactory;

public class PlayerSpecificGame {

	private Game originalGame;
	private Player viewingPlayer;

	public PlayerSpecificGame(Game originalGame, Player viewingPlayer) {
		this.originalGame = originalGame;
		this.viewingPlayer = viewingPlayer;
	}

	public int getId() {
		return originalGame.getId();
	}

	public void addComputerPlayer() {
		int numOtherPlayers = getOtherPlayers().size();
		String name = "Computer " + (numOtherPlayers + 1);
		ComputerPlayer player = new ComputerPlayer(name, new DefaultBuildingStrategy(), new AggressiveFleetStrategy());
		originalGame.addPlayer(player);
	}

	public GameStatus getStatus() {
		return originalGame.getStatus();
	}

	public PlayerSpecificTurnEvents getPreviousTurnEvents() {
		TurnEvents events = originalGame.getPreviousTurnEvents();
		if (events != null) {
			return new PlayerSpecificTurnEvents(events, viewingPlayer);
		}
		return null;
	}

	public void start() {
		originalGame.setUniverse(UniverseFactory.generate(originalGame.getPlayers()));
		originalGame.start();
	}

	public void endTurn() {
		originalGame.endTurn(viewingPlayer);
	}

	public PlayerSpecificUniverse getUniverse() {
		if (originalGame.getUniverse() != null) {
			return PlayerSpecificUniverse.convert(originalGame.getUniverse(), viewingPlayer);
		}
		return null;
	}

	public OwnPlayer getMe() {
		return new OwnPlayer(viewingPlayer);
	}

	public List<OtherPlayer> getOtherPlayers() {
		List<Player> otherOriginalPlayers = Player.filterAllOtherPlayers(originalGame.getPlayers(), viewingPlayer);
		return OtherPlayer.convert(otherOriginalPlayers);
	}
}
