package de.spries.fleetcommander.rest;

import java.util.Arrays;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.spries.fleetcommander.model.player.Player;
import de.spries.fleetcommander.model.universe.Universe;
import de.spries.fleetcommander.model.universe.UniverseGenerator;

@Path("")
public class GameService {

	@GET
	@Path("games/100/universe")
	@Produces(MediaType.APPLICATION_JSON)
	public Universe getUniverse() {
		return UniverseGenerator.generate(Arrays.asList(new Player("Player 1")));
	}
}
