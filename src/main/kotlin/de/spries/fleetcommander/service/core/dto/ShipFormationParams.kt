package de.spries.fleetcommander.service.core.dto

import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.builder.ToStringStyle

data class ShipFormationParams(val shipCount: Int = 0, val originPlanetId: Int = 0, val destinationPlanetId: Int = 0) {

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE)
    }
}