package de.spries.fleetcommander.model.facade;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import de.spries.fleetcommander.model.core.Game;
import de.spries.fleetcommander.model.core.Player;

public class PlayerSpecificGame {

	private Game originalGame;
	private Player viewingPlayer;
	private PlayerSpecificUniverse specificUniverse;
	private OwnPlayer me;
	private Collection<OtherPlayer> otherPlayers;

	public PlayerSpecificGame(Game originalGame, Player viewingPlayer) {
		this.originalGame = originalGame;
		this.viewingPlayer = viewingPlayer;
		specificUniverse = PlayerSpecificUniverse.convert(originalGame.getUniverse(), viewingPlayer);
		me = new OwnPlayer(viewingPlayer);
		List<Player> otherOriginalPlayers = originalGame.getPlayers().parallelStream()
				.filter((p) -> !p.equals(viewingPlayer)).collect(Collectors.toList());
		otherPlayers = OtherPlayer.convert(otherOriginalPlayers);
	}

	public int getId() {
		return originalGame.getId();
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

	public Collection<OtherPlayer> getOtherPlayers() {
		return otherPlayers;
	}
}
