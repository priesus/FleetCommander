package de.spries.fleetcommander.model.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.spries.fleetcommander.model.core.universe.TurnEventBus;

public class TurnEvents implements TurnEventBus {

	public class PlayerTurnEvents {

		private int conqueredEnemyPlanets = 0;
		private int conqueredUninhabitedPlanets = 0;
		private int defendedPlanets = 0;
		private int lostShipFormations = 0;
		private int lostPlanets = 0;

		public void reset() {
			conqueredEnemyPlanets = 0;
			conqueredUninhabitedPlanets = 0;
			defendedPlanets = 0;
			lostShipFormations = 0;
			lostPlanets = 0;
		}

		public boolean hasEvents() {
			int sumEvents = conqueredEnemyPlanets + conqueredUninhabitedPlanets + defendedPlanets + lostShipFormations
					+ lostPlanets;
			return 0 < sumEvents;
		}

	}

	private Map<Player, PlayerTurnEvents> events;

	public TurnEvents(Collection<Player> players) {
		events = new HashMap<>();
		players.stream().forEach(p -> events.put(p, new PlayerTurnEvents()));
	}

	protected void clear() {
		events.forEach((player, playerEvents) -> playerEvents.reset());
	}

	public boolean hasEvents(Player viewingPlayer) {
		return events.get(viewingPlayer).hasEvents();
	}

	public int getConqueredEnemyPlanets(Player affectedPlayer) {
		return events.get(affectedPlayer).conqueredEnemyPlanets;
	}

	public int getConqueredUninhabitedPlanets(Player affectedPlayer) {
		return events.get(affectedPlayer).conqueredUninhabitedPlanets;
	}

	public int getDefendedPlanets(Player affectedPlayer) {
		return events.get(affectedPlayer).defendedPlanets;
	}

	public int getLostShipFormations(Player affectedPlayer) {
		return events.get(affectedPlayer).lostShipFormations;
	}

	public int getLostPlanets(Player affectedPlayer) {
		return events.get(affectedPlayer).lostPlanets;
	}

	@Override
	public void fireConqueredEnemyPlanet(Player affectedPlayer) {
		events.get(affectedPlayer).conqueredEnemyPlanets++;
	}

	@Override
	public void fireConqueredUninhabitedPlanet(Player affectedPlayer) {
		events.get(affectedPlayer).conqueredUninhabitedPlanets++;
	}

	@Override
	public void fireDefendedPlanet(Player affectedPlayer) {
		events.get(affectedPlayer).defendedPlanets++;
	}

	@Override
	public void fireLostShipFormation(Player affectedPlayer) {
		events.get(affectedPlayer).lostShipFormations++;
	}

	@Override
	public void fireLostPlanet(Player affectedPlayer) {
		events.get(affectedPlayer).lostPlanets++;
	}
}
