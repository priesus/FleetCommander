package de.spries.fleetcommander.service.rest;

import static com.jayway.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_ACCEPTED;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

public class GamesRestServiceIT {

	private static final String SEND_SHIPS_REQUEST_BODY = "{\"shipCount\": %d, \"originPlanetId\": %d, \"destinationPlanetId\": %d}";
	private String gameUrl;
	private String gameAuthToken;

	@Before
	public void setUp() {
		Response response = when("{\"playerName\": \"test-player\"}").post("/rest/games");

		gameUrl = response.getHeader("Location");
		gameAuthToken = response.getBody().jsonPath().getString("fullAuthToken");

		assertThat(gameUrl, startsWith("http://localhost/rest/games/"));
		assertThat(gameAuthToken, is(notNullValue()));
		assertThat(response.getStatusCode(), is(SC_CREATED));

		whenAuthorized().post(gameUrl + "/players").then().statusCode(SC_ACCEPTED);
		whenAuthorized("{\"isStarted\": true}").post(gameUrl).then().statusCode(SC_ACCEPTED);
	}

	@Test
	public void cannotGetGameWithoutAuthorization() throws Exception {
		when().get(gameUrl)
				.then().statusCode(SC_BAD_REQUEST);
	}

	@Test
	public void cannotGetGameWithWrongAuthorization() throws Exception {
		whenAuthorizedWrong().get(gameUrl)
				.then().statusCode(SC_UNAUTHORIZED);
	}

	@Test
	public void cannotGetJoinCodesWithoutAuthorization() throws Exception {
		when().get(gameUrl + "/joinCodes")
				.then().statusCode(SC_BAD_REQUEST);
	}

	@Test
	public void cannotGetJoinCodesWithWrongAuthorization() throws Exception {
		whenAuthorizedWrong().get(gameUrl + "/joinCodes")
				.then().statusCode(SC_UNAUTHORIZED);
	}

	@Test
	public void cannotCreateJoinCodesWithoutAuthorization() throws Exception {
		when().post(gameUrl + "/joinCodes")
				.then().statusCode(SC_BAD_REQUEST);
	}

	@Test
	public void cannotCreateJoinCodesWithWrongAuthorization() throws Exception {
		whenAuthorizedWrong().post(gameUrl + "/joinCodes")
				.then().statusCode(SC_UNAUTHORIZED);
	}

	@Test
	public void canJoinGameViaValidCode() throws Exception {
		Response response = when("{\"playerName\": \"test-player\"}").post("/rest/games");

		gameUrl = response.getHeader("Location");
		gameAuthToken = response.getBody().jsonPath().getString("fullAuthToken");

		whenAuthorized().post(gameUrl + "/joinCodes")
				.then().statusCode(SC_CREATED);

		Response codesResponse = whenAuthorized().get(gameUrl + "/joinCodes");
		assertThat(codesResponse.getStatusCode(), is(SC_OK));

		List<String> joinCodes = codesResponse.getBody().jsonPath().getList("joinCodes");
		assertThat(joinCodes, hasSize(1));

		Response joinResponse = when("{\"playerName\":\"new-test-player\", \"joinCode\": \"" + joinCodes.get(0) + "\"}")
				.post("/rest/games");

		gameUrl = joinResponse.getHeader("Location");
		gameAuthToken = joinResponse.getBody().jsonPath().getString("fullAuthToken");

		assertThat(gameUrl, startsWith("http://localhost/rest/games/"));
		assertThat(gameAuthToken, is(notNullValue()));
		assertThat(joinResponse.getStatusCode(), is(SC_CREATED));
	}

	@Test
	public void cannotJoinGameWithDuplicatePlayerName() throws Exception {
		Response response = when("{\"playerName\": \"test-player\"}").post("/rest/games");

		gameUrl = response.getHeader("Location");
		gameAuthToken = response.getBody().jsonPath().getString("fullAuthToken");

		whenAuthorized().post(gameUrl + "/joinCodes")
				.then().statusCode(SC_CREATED);

		Response codesResponse = whenAuthorized().get(gameUrl + "/joinCodes");
		assertThat(codesResponse.getStatusCode(), is(SC_OK));

		List<String> joinCodes = codesResponse.getBody().jsonPath().getList("joinCodes");
		assertThat(joinCodes, hasSize(1));

		when("{\"playerName\":\"test-player\", \"joinCode\": \"" + joinCodes.get(0) + "\"}")
				.post("/rest/games")
				.then().statusCode(SC_CONFLICT);
	}

	@Test
	public void cannotJoinGameViaInvalidCode() throws Exception {
		when("{\"playerName\": \"test-player-2\", \"joinCode\": \"invalidJoinCode\"}").post("/rest/games")
				.then().statusCode(SC_NOT_FOUND)
				.and().body("error", is("invalidjoincode is an invalid code"));
	}

	@Test
	public void universeIsReturned() throws Exception {
		whenAuthorized().get(gameUrl)
				.then().statusCode(SC_OK)
				.and().body("universe", is(notNullValue()));
	}

	@Test
	public void cannotEndTurnWithoutAuthorization() throws Exception {
		when().post(gameUrl + "/turns")
				.then().statusCode(SC_BAD_REQUEST);
	}

	@Test
	public void canEndTurnWithAuthorization() throws Exception {
		whenAuthorized().post(gameUrl + "/turns")
				.then().statusCode(SC_ACCEPTED);
	}

	@Test
	public void cannotSendShipsWithoutAuthorization() throws Exception {
		String body = String.format(SEND_SHIPS_REQUEST_BODY, 1, 1, 2);
		when(body).post(gameUrl + "/universe/travellingShipFormations")
				.then().statusCode(SC_BAD_REQUEST);
	}

	@Test
	public void canSendShipsWithAuthorization() throws Exception {
		Response response = whenAuthorized().get(gameUrl);
		int homePlanetId = response.getBody().jsonPath().getInt("universe.homePlanet.id");
		int otherPlanetId = (homePlanetId + 1) % 20;

		String body = String.format(SEND_SHIPS_REQUEST_BODY, 1, homePlanetId, otherPlanetId);
		whenAuthorized(body).post(gameUrl + "/universe/travellingShipFormations")
				.then().statusCode(SC_ACCEPTED);
	}

	@Test
	public void cannotSendShipsFromWrongPlanet() throws Exception {
		Response response = whenAuthorized().get(gameUrl);
		int homePlanetId = response.getBody().jsonPath().getInt("universe.homePlanet.id");
		int otherPlanetId = (homePlanetId + 1) % 20;

		String body = String.format(SEND_SHIPS_REQUEST_BODY, 1, otherPlanetId, homePlanetId);
		whenAuthorized(body).post(gameUrl + "/universe/travellingShipFormations")
				.then().statusCode(SC_CONFLICT);
	}

	@Test
	public void cannotBuildFactoryWithoutAuthorization() throws Exception {
		when().post(gameUrl + "/universe/planets/1/factories")
				.then().statusCode(SC_BAD_REQUEST);
	}

	@Test
	public void canBuildFactoryWithAuthorization() throws Exception {
		Response response = whenAuthorized().get(gameUrl);
		int homePlanetId = response.getBody().jsonPath().getInt("universe.homePlanet.id");

		whenAuthorized().post(gameUrl + "/universe/planets/" + homePlanetId + "/factories")
				.then().statusCode(SC_ACCEPTED);
	}

	@Test
	public void cannotBuildFactoryOnWrongPlanet() throws Exception {
		Response response = whenAuthorized().get(gameUrl);
		int homePlanetId = response.getBody().jsonPath().getInt("universe.homePlanet.id");
		int otherPlanetId = (homePlanetId + 1) % 20;

		whenAuthorized().post(gameUrl + "/universe/planets/" + otherPlanetId + "/factories")
				.then().statusCode(SC_CONFLICT);
	}

	@Test
	public void cannotChangeProductionFocusWithoutAuthorization() throws Exception {
		when("{\"productionFocus\":1}").post(gameUrl + "/universe/planets/1")
				.then().statusCode(SC_BAD_REQUEST);
	}

	@Test
	public void canChangeProductionFocusWithAuthorization() throws Exception {
		Response response = whenAuthorized().get(gameUrl);
		int homePlanetId = response.getBody().jsonPath().getInt("universe.homePlanet.id");

		whenAuthorized("{\"productionFocus\":1}").post(gameUrl + "/universe/planets/" + homePlanetId)
				.then().statusCode(SC_ACCEPTED);
	}

	@Test
	public void cannotChangeProductionFocusOnWrongPlanet() throws Exception {
		Response response = whenAuthorized().get(gameUrl);
		int homePlanetId = response.getBody().jsonPath().getInt("universe.homePlanet.id");
		int otherPlanetId = (homePlanetId + 1) % 20;

		whenAuthorized("{\"productionFocus\":1}").post(gameUrl + "/universe/planets/" + otherPlanetId)
				.then().statusCode(SC_CONFLICT);
	}

	@Test
	public void cannotQuitGameWithoutAuthorization() throws Exception {
		when().delete(gameUrl)
				.then().statusCode(SC_BAD_REQUEST);
	}

	@Test
	public void canQuitGameWithAuthorization() throws Exception {
		whenAuthorized().delete(gameUrl)
				.then().statusCode(SC_ACCEPTED);
	}

	@Test
	public void quittingGameInvalidatesToken() throws Exception {
		whenAuthorized().delete(gameUrl)
				.then().statusCode(SC_ACCEPTED);

		whenAuthorized().get(gameUrl)
				.then().statusCode(SC_UNAUTHORIZED);
		whenAuthorized().post(gameUrl + "/turns")
				.then().statusCode(SC_UNAUTHORIZED);
		whenAuthorized().post(gameUrl + "/universe/travellingShipFormations/1/1/2")
				.then().statusCode(SC_UNAUTHORIZED);
		whenAuthorized().delete(gameUrl)
				.then().statusCode(SC_UNAUTHORIZED);
	}

	private RequestSpecification when() {
		return when(null);
	}

	private RequestSpecification when(String jsonBody) {
		RequestSpecification spec = given().port(80).contentType(ContentType.JSON);
		if (jsonBody != null) {
			spec = spec.body(jsonBody);
		}
		return spec.when();
	}

	private RequestSpecification whenAuthorized() {
		return whenAuthorized(null);
	}

	private RequestSpecification whenAuthorizedWrong() {
		return when().header("Authorization", "Bearer " + gameAuthToken + "1");
	}

	private RequestSpecification whenAuthorized(String jsonBody) {
		return when(jsonBody).header("Authorization", "Bearer " + gameAuthToken);
	}

}
