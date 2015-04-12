package de.spries.fleetcommander.facade;

import java.util.List;
import java.util.stream.Collectors;

import de.spries.fleetcommander.model.Player;

public class PlayerSpecificPlayer {

	private Player originalPlayer;
	private Player viewingPlayer;

	public PlayerSpecificPlayer(Player originalPlayer, Player viewingPlayer) {
		this.originalPlayer = originalPlayer;
		this.viewingPlayer = viewingPlayer;
	}

	public String getName() {
		return originalPlayer.getName();
	}

	public Integer getCredits() {
		if (originalPlayer.equals(viewingPlayer)) {
			return originalPlayer.getCredits();
		}
		return null;
	}

	protected static List<PlayerSpecificPlayer> convert(List<Player> players, Player viewingPlayer) {
		return players.parallelStream()
				.map((p) -> new PlayerSpecificPlayer(p, viewingPlayer))
				.collect(Collectors.toList());
	}

}
