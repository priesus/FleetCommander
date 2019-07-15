package de.spries.fleetcommander.service.rest

import de.spries.fleetcommander.service.core.GameAuthenticator
import de.spries.fleetcommander.service.core.dto.GamePlayer
import de.spries.fleetcommander.service.rest.errorhandling.RestError
import org.apache.logging.log4j.LogManager
import java.io.IOException
import java.util.regex.Pattern
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter
import javax.ws.rs.container.PreMatching
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.Response
import javax.ws.rs.ext.Provider

/**
 * This request filter is supposed to block unauthorized access to game data.
 * Clients must provide a header "Authorization" with the value "Bearer *security-token*" in order to access this data.
 */
@Provider
@PreMatching
class GameAccessTokenFilter : ContainerRequestFilter {

    @Throws(IOException::class)
    override fun filter(requestCtx: ContainerRequestContext) {
        val path = requestCtx.uriInfo.path

        val protectedPathMatcher = PROTECTED_PATHS.matcher(path)
        if (protectedPathMatcher.find()) {

            val gameId = Integer.parseInt(protectedPathMatcher.group("gameId"))

            var playerId = 0
            var token: String? = null
            try {
                playerId = extractPlayerIdFromContext(requestCtx)
                token = extractAuthTokenFromContext(requestCtx)
            } catch (e: Exception) {
                LOGGER.warn("Invalid request headers (playerId/token couldn't be extracted)", e)
                requestCtx.abortWith(Response.status(Response.Status.BAD_REQUEST)
                        .entity(RestError("Required header 'Authorization' was missing or malformed." + " Expexted value: 'Bearer <playerId>:<authToken>'")).build())
            }

            val gamePlayer = GamePlayer.forIds(gameId, playerId)
            if (!GameAuthenticator.INSTANCE.isAuthTokenValid(gamePlayer, token)) {
                LOGGER.warn("{}: Unauthorized access with token {}", gamePlayer, token)
                requestCtx.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                        .entity(RestError("'Authorization' header was invalid for game $gameId")).build())
            }
        }
    }

    companion object {

        private val LOGGER = LogManager.getLogger(GameAccessTokenFilter::class.java.name)

        const val AUTH_HEADER = "Authorization"
        const val AUTH_TOKEN_PREFIX = "Bearer "
        private val PROTECTED_PATHS = Pattern.compile("^games/(?<gameId>\\d+)")

        private fun extractPlayerIdFromContext(requestCtx: ContainerRequestContext): Int {
            val playerToken = extractAuthFromContext(requestCtx)
            val playerAndToken = playerToken!!.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            return Integer.parseInt(playerAndToken[0])
        }

        fun extractPlayerIdFromHeaders(headers: HttpHeaders): Int {
            val playerToken = extractAuthFromHeaders(headers)
            val playerAndToken = playerToken!!.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            return Integer.parseInt(playerAndToken[0])
        }

        private fun extractAuthTokenFromContext(requestCtx: ContainerRequestContext): String {
            val playerToken = extractAuthFromContext(requestCtx)
            val playerAndToken = playerToken!!.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            return playerAndToken[1]
        }

        private fun extractAuthFromContext(requestCtx: ContainerRequestContext): String? {
            val token = requestCtx.getHeaderString(AUTH_HEADER)
            return extractTokenFromHeaderValue(token)
        }

        private fun extractAuthFromHeaders(headers: HttpHeaders): String? {
            val token = headers.getHeaderString(AUTH_HEADER)
            return extractTokenFromHeaderValue(token)
        }

        private fun extractTokenFromHeaderValue(token: String?): String? {
            return if (token != null && token.startsWith(AUTH_TOKEN_PREFIX)) {
                token.substring(AUTH_TOKEN_PREFIX.length)
            } else null
        }
    }

}
