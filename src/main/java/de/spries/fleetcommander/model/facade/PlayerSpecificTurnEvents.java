package de.spries.fleetcommander.model.facade;

import de.spries.fleetcommander.model.core.Player;
import de.spries.fleetcommander.model.core.TurnEvents;

public class PlayerSpecificTurnEvents {

	private TurnEvents originalEvents;
	private Player viewingPlayer;

	public PlayerSpecificTurnEvents(TurnEvents originalEvents, Player viewingPlayer) {
		this.originalEvents = originalEvents;
		this.viewingPlayer = viewingPlayer;
	}

	public int getConqueredEnemyPlanets() {
		return originalEvents.getConqueredEnemyPlanets(viewingPlayer);
	}

	public int getConqueredUninhabitedPlanets() {
		return originalEvents.getConqueredUninhabitedPlanets(viewingPlayer);
	}

	public int getLostShipFormations() {
		return originalEvents.getLostShipFormations(viewingPlayer);
	}

	public int getDefendedPlanets() {
		return originalEvents.getDefendedPlanets(viewingPlayer);
	}

	public int getLostPlanets() {
		return originalEvents.getLostPlanets(viewingPlayer);
	}

	public boolean getHasEvents() {
		return originalEvents.hasEvents(viewingPlayer);
	}
}
