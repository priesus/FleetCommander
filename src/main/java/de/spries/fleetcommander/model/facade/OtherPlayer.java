package de.spries.fleetcommander.model.facade;

import java.util.List;
import java.util.stream.Collectors;

import de.spries.fleetcommander.model.core.Player;
import de.spries.fleetcommander.model.core.Player.Status;

public class OtherPlayer {

	protected Player originalPlayer;

	public OtherPlayer(Player originalPlayer) {
		this.originalPlayer = originalPlayer;
	}

	public String getName() {
		return originalPlayer.getName();
	}

	public Status getStatus() {
		return originalPlayer.getStatus();
	}

	public boolean isHumanPlayer() {
		return originalPlayer.isHumanPlayer();
	}

	protected static List<OtherPlayer> convert(List<Player> otherPlayers) {
		return otherPlayers.stream()
				.map(p -> new OtherPlayer(p))
				.collect(Collectors.toList());
	}

}
