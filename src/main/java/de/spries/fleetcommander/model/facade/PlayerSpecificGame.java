package de.spries.fleetcommander.model.facade;

import java.util.List;

import de.spries.fleetcommander.model.core.ComputerPlayer;
import de.spries.fleetcommander.model.core.Game;
import de.spries.fleetcommander.model.core.Player;
import de.spries.fleetcommander.model.core.universe.UniverseFactory;

public class PlayerSpecificGame {

	private Game originalGame;
	private Player viewingPlayer;
	private PlayerSpecificUniverse specificUniverse;
	private OwnPlayer me;
	private List<OtherPlayer> otherPlayers;

	public PlayerSpecificGame(Game originalGame, Player viewingPlayer) {
		this.originalGame = originalGame;
		this.viewingPlayer = viewingPlayer;

		me = new OwnPlayer(viewingPlayer);
		if (originalGame.getUniverse() != null) {
			specificUniverse = PlayerSpecificUniverse.convert(originalGame.getUniverse(), viewingPlayer);
		}
		List<Player> otherOriginalPlayers = Player.filterAllOtherPlayers(originalGame.getPlayers(), viewingPlayer);
		otherPlayers = OtherPlayer.convert(otherOriginalPlayers);
	}

	public int getId() {
		return originalGame.getId();
	}

	public void addComputerPlayer() {
		int numOtherPlayers = getOtherPlayers().size() + 1;
		originalGame.addPlayer(new ComputerPlayer("Computer " + numOtherPlayers));
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
		return specificUniverse;
	}

	public OwnPlayer getMe() {
		return me;
	}

	public List<OtherPlayer> getOtherPlayers() {
		return otherPlayers;
	}
}
