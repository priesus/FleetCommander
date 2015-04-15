package de.spries.fleetcommander.model.core.common;

public class IllegalActionException extends RuntimeException {

	@Deprecated
	public IllegalActionException() {
		// default with no msg
	}

	public IllegalActionException(String msg) {
		super(msg);
	}
}
