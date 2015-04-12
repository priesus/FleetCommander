package de.spries.fleetcommander.facade;

import java.util.Collection;

import de.spries.fleetcommander.model.Game;
import de.spries.fleetcommander.model.Player;

public class PlayerSpecificGame {

	private Game originalGame;
	private Player viewingPlayer;
	private PlayerSpecificUniverse specificUniverse;
	private Collection<PlayerSpecificPlayer> specificPlayers;

	public PlayerSpecificGame(Game originalGame, Player viewingPlayer) {
		this.originalGame = originalGame;
		this.viewingPlayer = viewingPlayer;
		specificUniverse = PlayerSpecificUniverse.convert(originalGame.getUniverse(), viewingPlayer);
		specificPlayers = PlayerSpecificPlayer.convert(originalGame.getPlayers(), viewingPlayer);
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

	public Collection<PlayerSpecificPlayer> getPlayers() {
		return specificPlayers;
	}
}
