package de.spries.fleetcommander.service.core;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.spries.fleetcommander.model.core.Game;
import de.spries.fleetcommander.model.core.Player;
import de.spries.fleetcommander.model.core.common.IllegalActionException;
import de.spries.fleetcommander.model.facade.PlayerSpecificGame;
import de.spries.fleetcommander.persistence.GameStore;
import de.spries.fleetcommander.persistence.InvalidCodeException;
import de.spries.fleetcommander.persistence.JoinCodeLimitReachedException;
import de.spries.fleetcommander.persistence.JoinCodes;
import de.spries.fleetcommander.service.core.dto.GameAccessParams;
import de.spries.fleetcommander.service.core.dto.GameParams;
import de.spries.fleetcommander.service.core.dto.GamePlayer;
import de.spries.fleetcommander.service.core.dto.ShipFormationParams;

public class GamesService {

	private static final Logger LOGGER = LogManager.getLogger(GamesService.class.getName());

	public GameAccessParams createNewGame(String playerName) {
		Game game = new Game();
		Player p = new Player(playerName);
		game.addPlayer(p);

		int gameId = GameStore.INSTANCE.create(game);
		game.setId(gameId);
		GamePlayer gamePlayer = GamePlayer.forIds(gameId, p.getId());
		String authToken = GameAuthenticator.INSTANCE.createAuthToken(gamePlayer);

		LOGGER.debug("{}: Created", gamePlayer);

		return new GameAccessParams(gamePlayer, authToken);
	}

	public String createJoinCode(int gameId) throws JoinCodeLimitReachedException {
		return JoinCodes.INSTANCE.create(gameId);
	}

	public Collection<String> getActiveJoinCodes(int gameId) {
		return JoinCodes.INSTANCE.get(gameId);
	}

	public GameAccessParams joinGame(String playerName, String joinCode) throws InvalidCodeException {
		int gameId = JoinCodes.INSTANCE.invalidate(joinCode);
		Game game = GameStore.INSTANCE.get(gameId);
		Player player = new Player(playerName);
		game.addPlayer(player);
		GamePlayer gamePlayer = new GamePlayer(gameId, player.getId());
		String authToken = GameAuthenticator.INSTANCE.createAuthToken(gamePlayer);

		LOGGER.debug("{}: Joined", gamePlayer);

		return new GameAccessParams(gamePlayer, authToken);
	}

	public PlayerSpecificGame getGame(GamePlayer gamePlayer) {
		Game game = GameStore.INSTANCE.get(gamePlayer.getGameId());
		LOGGER.debug("{}: Get", gamePlayer);
		if (game != null) {
			Player player = game.getPlayerWithId(gamePlayer.getPlayerId());
			if (player != null) {
				return new PlayerSpecificGame(game, player);
			}
			LOGGER.warn("{}: Get, but doesn't participate", gamePlayer);
			throw new IllegalActionException("You're not participating in this game");
		}
		LOGGER.warn("{}: Get, but doesn't exist", gamePlayer);
		throw new IllegalActionException("The game doesn't exist on the server");
	}

	public void quitGame(GamePlayer gamePlayer) {
		LOGGER.debug("{}: Delete", gamePlayer);
		GameAuthenticator.INSTANCE.deleteAuthToken(gamePlayer);
		PlayerSpecificGame game = getGame(gamePlayer);
		game.quit();
	}

	public void addComputerPlayer(GamePlayer gamePlayer) {
		LOGGER.debug("{}: Add computer player", gamePlayer);
		PlayerSpecificGame game = getGame(gamePlayer);
		game.addComputerPlayer();
	}

	public void modifyGame(GamePlayer gamePlayer, GameParams params) {
		LOGGER.debug("{}: Modify with params {}", gamePlayer, params);
		if (Boolean.TRUE.equals(params.getIsStarted())) {
			PlayerSpecificGame game = getGame(gamePlayer);
			game.start();
			JoinCodes.INSTANCE.invalidateAll(gamePlayer.getGameId());
		}
	}

	public void endTurn(GamePlayer gamePlayer) {
		LOGGER.debug("{}: End turn", gamePlayer);
		PlayerSpecificGame game = getGame(gamePlayer);
		game.endTurn();
	}

	public void sendShips(GamePlayer gamePlayer, ShipFormationParams ships) {
		LOGGER.debug("{}: Send ships with params {}", gamePlayer, ships);
		PlayerSpecificGame game = getGame(gamePlayer);
		game.getUniverse().sendShips(ships.getShipCount(), ships.getOriginPlanetId(),
				ships.getDestinationPlanetId());
	}

	public void buildFactory(GamePlayer gamePlayer, int planetId) {
		LOGGER.debug("{}: Build factory on planet {}", gamePlayer, planetId);
		PlayerSpecificGame game = getGame(gamePlayer);
		game.getUniverse().getPlanet(planetId).buildFactory();
	}
}
