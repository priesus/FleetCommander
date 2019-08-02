package de.spries.fleetcommander.web

import de.spries.fleetcommander.model.core.common.IllegalActionException
import de.spries.fleetcommander.web.dto.GamePlayer
import mu.KotlinLogging
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import java.util.regex.Pattern
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

@Component
class GameAccessTokenFilter(private val gamesAuthenticator: GameAuthenticator) : Filter {

    private val log = KotlinLogging.logger {}

    @Override
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {

        val req = request as HttpServletRequest
        val path = req.requestURI

        val protectedPathMatcher = PROTECTED_PATHS.matcher(path)
        if (protectedPathMatcher.find()) {

            val gameId = Integer.parseInt(protectedPathMatcher.group("gameId"))

            val playerId: Int
            val token: String?
            try {
                playerId = extractPlayerIdFromContext(req)
                token = extractAuthTokenFromContext(req)

            } catch (e: Exception) {
                log.warn("Invalid request headers (playerId/token couldn't be extracted)", e)
                throw IllegalActionException("Required header 'Authorization' was missing or malformed." + " Expexted value: 'Bearer <playerId>:<authToken>'")
            }

            val gamePlayer = GamePlayer(gameId, playerId)
            if (!gamesAuthenticator.isAuthTokenValid(gamePlayer, token)) {
                log.warn("{}: Unauthorized access with token {}", gamePlayer, token)
                throw IllegalActionException("'Authorization' header was invalid for game $gameId")
            }
        }

        chain.doFilter(request, response)
    }


    companion object {

        private const val AUTH_HEADER = "Authorization"
        private const val AUTH_TOKEN_PREFIX = "Bearer "
        private val PROTECTED_PATHS = Pattern.compile("^games/(?<gameId>\\d+)")

        private fun extractPlayerIdFromContext(requestCtx: HttpServletRequest): Int {
            val playerToken = extractAuthFromContext(requestCtx)
            val playerAndToken = playerToken!!.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            return Integer.parseInt(playerAndToken[0])
        }

        fun extractPlayerIdFromHeaders(headers: HttpHeaders): Int {
            val playerToken = extractAuthFromHeaders(headers)
            val playerAndToken = playerToken!!.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            return Integer.parseInt(playerAndToken[0])
        }

        private fun extractAuthTokenFromContext(requestCtx: HttpServletRequest): String {
            val playerToken = extractAuthFromContext(requestCtx)
            val playerAndToken = playerToken!!.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            return playerAndToken[1]
        }

        private fun extractAuthFromContext(requestCtx: HttpServletRequest): String? {
            val token = requestCtx.getHeader(AUTH_HEADER)
            return extractTokenFromHeaderValue(token)
        }

        private fun extractAuthFromHeaders(headers: HttpHeaders): String? {
            val token = headers.getValue(AUTH_HEADER).firstOrNull()
            return extractTokenFromHeaderValue(token)
        }

        private fun extractTokenFromHeaderValue(token: String?): String? {
            return if (token != null && token.startsWith(AUTH_TOKEN_PREFIX)) {
                token.substring(AUTH_TOKEN_PREFIX.length)
            } else null
        }
    }
}