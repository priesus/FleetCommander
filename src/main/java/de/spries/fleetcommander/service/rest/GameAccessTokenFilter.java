package de.spries.fleetcommander.service.rest;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import de.spries.fleetcommander.service.core.GameAuthenticator;

/**
 * This request filter is supposed to block unauthorized access to game data.
 * Clients must provide a header "Authorization" with the value "Bearer *security-token*" in order to access this data.
 */
@Provider
@PreMatching
public class GameAccessTokenFilter implements ContainerRequestFilter {

	public static final String AUTH_HEADER = "Authorization";
	public static final String AUTH_TOKEN_PREFIX = "Bearer ";
	private static final Pattern PROTECTED_PATHS = Pattern.compile("^games/(?<gameId>\\d+)");

	@Override
	public void filter(ContainerRequestContext requestCtx) throws IOException {
		String path = requestCtx.getUriInfo().getPath();

		Matcher protectedPathMatcher = PROTECTED_PATHS.matcher(path);
		if (protectedPathMatcher.find()) {

			int gameId = Integer.parseInt(protectedPathMatcher.group("gameId"));

			String token = extractAuthTokenFromContext(requestCtx);
			if (!GameAuthenticator.INSTANCE.isAuthTokenValid(gameId, token)) {
				requestCtx.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
			}
		}
	}

	protected static String extractAuthTokenFromContext(ContainerRequestContext requestCtx) {
		String token = requestCtx.getHeaderString(AUTH_HEADER);
		return extractTokenFromHeaderValue(token);
	}

	protected static String extractAuthTokenFromHeaders(HttpHeaders httpHeaders) {
		String token = httpHeaders.getHeaderString(AUTH_HEADER);
		return extractTokenFromHeaderValue(token);
	}

	private static String extractTokenFromHeaderValue(String token) {
		if (token != null && token.startsWith(AUTH_TOKEN_PREFIX)) {
			return token.substring(AUTH_TOKEN_PREFIX.length());
		}
		return null;
	}

}
