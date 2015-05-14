package de.spries.fleetcommander.service.rest.errorhandling;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import de.spries.fleetcommander.model.core.common.IllegalActionException;
import de.spries.fleetcommander.service.rest.GamesRestService;

@Provider
public class IllegalActionExceptionHandler implements ExceptionMapper<IllegalActionException> {

	@Override
	public Response toResponse(IllegalActionException e) {
		RestError error = new RestError(e.getMessage());
		return GamesRestService.noCacheResponse(Response.Status.CONFLICT).entity(error).build();
	}

}
