package de.spries.fleetcommander.service.core;

import de.spries.fleetcommander.model.core.Game;
import de.spries.fleetcommander.model.core.Player;
import de.spries.fleetcommander.model.facade.PlayerSpecificGame;
import de.spries.fleetcommander.persistence.GameStore;

public class GamesService {

	public GameAccessParams createNewGame(String playerName) {
		Game game = new Game();
		Player p = new Player(playerName);
		game.addPlayer(p);

		int gameId = GameStore.INSTANCE.create(game);
		game.setId(gameId);
		String authToken = GameAuthenticator.INSTANCE.createAuthToken(gameId);

		return new GameAccessParams(gameId, authToken);
	}

	public PlayerSpecificGame getGame(int gameId, String playerId) {
		Game game = GameStore.INSTANCE.get(gameId);
		Player player = game.getPlayers().get(0);
		return new PlayerSpecificGame(game, player);
	}

	public void deleteGame(int gameId, String authToken) {
		GameAuthenticator.INSTANCE.deleteAuthToken(gameId, authToken);
		GameStore.INSTANCE.delete(gameId);
	}
}
