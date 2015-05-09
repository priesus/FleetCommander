package de.spries.fleetcommander.service.core.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class GameAccessParams {

	private GamePlayer gamePlayer;
	private String authToken;

	public GameAccessParams(GamePlayer gamePlayer, String authToken) {
		this.gamePlayer = gamePlayer;
		this.authToken = authToken;
	}

	public int getGameId() {
		return gamePlayer.getGameId();
	}

	@JsonIgnore
	public int getPlayerId() {
		return gamePlayer.getPlayerId();
	}

	@JsonIgnore
	public String getAuthToken() {
		return authToken;
	}

	public String getFullAuthToken() {
		return getPlayerId() + ":" + authToken;
	}

}
