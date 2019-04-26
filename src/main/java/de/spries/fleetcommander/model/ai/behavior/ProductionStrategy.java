package de.spries.fleetcommander.model.ai.behavior;

import de.spries.fleetcommander.model.facade.PlayerSpecificUniverse;

public interface ProductionStrategy {

    void updateProductionFocus(PlayerSpecificUniverse universe, int availablePlayerCredits);
}
