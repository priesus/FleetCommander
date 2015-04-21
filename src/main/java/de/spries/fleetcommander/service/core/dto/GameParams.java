package de.spries.fleetcommander.service.core.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class GameParams {
	private Boolean isStarted;

	public Boolean getIsStarted() {
		return isStarted;
	}

	public void setIsStarted(Boolean isStarted) {
		this.isStarted = isStarted;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
