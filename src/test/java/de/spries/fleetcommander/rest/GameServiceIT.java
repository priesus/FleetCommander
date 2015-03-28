package de.spries.fleetcommander.rest;

import static com.jayway.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

public class GameServiceIT {

	private String gameUrl;
	private String gameAuthToken;

	@Before
	public void setUp() {
		Response response = when().post("/rest/games");

		gameUrl = response.getHeader("Location");
		gameAuthToken = response.getBody().jsonPath().getString("gameAuthToken");

		assertThat(gameUrl, startsWith("http://localhost/rest/games/"));
		assertThat(gameAuthToken, is(notNullValue()));
		assertThat(response.getStatusCode(), is(SC_CREATED));
	}

	@Test
	public void cannotGetGameWithoutAuthorization() throws Exception {
		when().get(gameUrl)
				.then().statusCode(SC_UNAUTHORIZED);
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
				.then().statusCode(SC_UNAUTHORIZED);
	}

	@Test
	public void canEndTurnWithAuthorization() throws Exception {
		whenAuthorized().post(gameUrl + "/turns")
				.then().statusCode(SC_OK);
	}

	@Test
	public void cannotSendShipsWithoutAuthorization() throws Exception {
		when().post(gameUrl + "/universe/travellingShipFormations/1/1/2")
				.then().statusCode(SC_UNAUTHORIZED);
	}

	@Test
	public void cannotQuitGameWithoutAuthorization() throws Exception {
		when().delete(gameUrl)
				.then().statusCode(SC_UNAUTHORIZED);
	}

	@Test
	public void canQuitGameWithAuthorization() throws Exception {
		whenAuthorized().delete(gameUrl)
				.then().statusCode(SC_OK);
	}

	@Test
	public void quittingGameInvalidatesToken() throws Exception {
		whenAuthorized().delete(gameUrl)
				.then().statusCode(SC_OK);

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
		return given().port(80).when();
	}

	private RequestSpecification whenAuthorized() {
		return when().header("Authorization", gameAuthToken);
	}

}
