package de.spries.fleetcommander.service.core.dto;

public class GamePlayer {
	private int gameId;
	private int playerId;

	public GamePlayer(int gameId, int playerId) {
		this.gameId = gameId;
		this.playerId = playerId;
	}

	public int getGameId() {
		return gameId;
	}

	public int getPlayerId() {
		return playerId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + gameId;
		result = prime * result + playerId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		GamePlayer other = (GamePlayer) obj;
		if (gameId != other.gameId) {
			return false;
		}
		if (playerId != other.playerId) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "GamePlayer [gameId=" + gameId + ", playerId=" + playerId + "]";
	}

	public static GamePlayer forIds(int gameId, int playerId) {
		return new GamePlayer(gameId, playerId);
	}

}