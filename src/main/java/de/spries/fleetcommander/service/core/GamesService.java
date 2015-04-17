package de.spries.fleetcommander.service.core;

import de.spries.fleetcommander.model.core.Game;
import de.spries.fleetcommander.model.core.Player;
import de.spries.fleetcommander.model.facade.PlayerSpecificGame;
import de.spries.fleetcommander.persistence.GameStore;
import de.spries.fleetcommander.service.core.dto.GameAccessParams;
import de.spries.fleetcommander.service.core.dto.GameParams;
import de.spries.fleetcommander.service.core.dto.ShipFormationParams;

public class GamesService {

	//TODO write tests

	public GameAccessParams createNewGame(String playerName) {
		Game game = new Game();
		Player p = new Player(playerName);
		game.addPlayer(p);

		int gameId = GameStore.INSTANCE.create(game);
		game.setId(gameId);
		String authToken = GameAuthenticator.INSTANCE.createAuthToken(gameId);

		return new GameAccessParams(gameId, authToken);
	}

	public PlayerSpecificGame getGame(int gameId) {
		Game game = GameStore.INSTANCE.get(gameId);
		Player player = game.getPlayers().get(0);
		return new PlayerSpecificGame(game, player);
	}

	public void deleteGame(int gameId, String authToken) {
		GameAuthenticator.INSTANCE.deleteAuthToken(gameId, authToken);
		GameStore.INSTANCE.delete(gameId);
	}

	public void addComputerPlayer(int gameId) {
		PlayerSpecificGame game = getGame(gameId);
		game.addComputerPlayer();
	}

	public void startGame(int gameId, GameParams params) {
		if (params.getIsStarted() == true) {
			PlayerSpecificGame game = getGame(gameId);
			game.start();
		}
	}

	public void endTurn(int gameId) {
		PlayerSpecificGame game = getGame(gameId);
		game.endTurn();
	}

	public void sendShips(int gameId, ShipFormationParams ships) {
		PlayerSpecificGame game = getGame(gameId);
		game.getUniverse().sendShips(ships.getShipCount(), ships.getOriginPlanetId(),
				ships.getDestinationPlanetId());
	}

	public void buildFactory(int gameId, int planetId) {
		PlayerSpecificGame game = getGame(gameId);
		game.getUniverse().getPlanet(planetId).buildFactory();
	}
}
