package de.spries.fleetcommander.service.rest.errorhandling

import de.spries.fleetcommander.model.core.common.IllegalActionException
import de.spries.fleetcommander.service.rest.GamesRestService
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider

@Provider
class IllegalActionExceptionHandler : ExceptionMapper<IllegalActionException> {

    override fun toResponse(e: IllegalActionException): Response {
        val error = RestError(e.message)
        return GamesRestService.noCacheResponse(Response.Status.CONFLICT).entity(error).build()
    }

}
