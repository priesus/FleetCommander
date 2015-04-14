package de.spries.fleetcommander.model.facade;

import java.util.List;
import java.util.stream.Collectors;

import de.spries.fleetcommander.model.core.Player;

public class OtherPlayer {

	protected Player originalPlayer;

	public OtherPlayer(Player originalPlayer) {
		this.originalPlayer = originalPlayer;
	}

	public String getName() {
		return originalPlayer.getName();
	}

	protected static List<OtherPlayer> convert(List<Player> otherPlayers) {
		return otherPlayers.parallelStream()
				.map((p) -> new OtherPlayer(p))
				.collect(Collectors.toList());
	}

}
