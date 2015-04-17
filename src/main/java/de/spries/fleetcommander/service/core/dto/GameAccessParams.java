package de.spries.fleetcommander.service.core.dto;

public class GameAccessParams {

	private int gameId;
	private String authToken;

	public GameAccessParams(int gameId, String authToken) {
		this.gameId = gameId;
		this.authToken = authToken;
	}

	public int getGameId() {
		return gameId;
	}

	public String getAuthToken() {
		return authToken;
	}

}
