package de.spries.fleetcommander.model.facade;

import java.util.List;

import de.spries.fleetcommander.model.core.ComputerPlayer;
import de.spries.fleetcommander.model.core.Game;
import de.spries.fleetcommander.model.core.Player;
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
		originalGame.addPlayer(new ComputerPlayer("Computer " + (numOtherPlayers + 1)));
	}

	public boolean isStarted() {
		return originalGame.isStarted();
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
