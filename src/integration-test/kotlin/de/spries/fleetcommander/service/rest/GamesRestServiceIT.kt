package de.spries.fleetcommander.service.rest

import com.jayway.restassured.RestAssured.given
import com.jayway.restassured.http.ContentType
import com.jayway.restassured.specification.RequestSpecification
import org.apache.http.HttpStatus.SC_ACCEPTED
import org.apache.http.HttpStatus.SC_BAD_REQUEST
import org.apache.http.HttpStatus.SC_CONFLICT
import org.apache.http.HttpStatus.SC_CREATED
import org.apache.http.HttpStatus.SC_NOT_FOUND
import org.apache.http.HttpStatus.SC_OK
import org.apache.http.HttpStatus.SC_UNAUTHORIZED
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.notNullValue
import org.hamcrest.Matchers.startsWith
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test

class GamesRestServiceIT {
    private var gameUrl: String? = null
    private var gameAuthToken: String? = null

    @Before
    fun setUp() {
        val response = `when`("{\"playerName\": \"test-player\"}").post("/api/games")

        gameUrl = response.getHeader("Location")
        gameAuthToken = response.body.jsonPath().getString("fullAuthToken")

        assertThat(gameUrl, startsWith("http://localhost"))
        assertThat(gameUrl, containsString("/api/games/"))
        assertThat(gameAuthToken, `is`(notNullValue()))
        assertThat(response.statusCode, `is`(SC_CREATED))

        whenAuthorized().post(gameUrl!! + "/players").then().statusCode(SC_ACCEPTED)
        whenAuthorized("{\"isStarted\": true}").post(gameUrl).then().statusCode(SC_ACCEPTED)
    }

    @Test
    @Throws(Exception::class)
    fun cannotGetGameWithoutAuthorization() {
        `when`().get(gameUrl)
                .then().statusCode(SC_BAD_REQUEST)
    }

    @Test
    @Throws(Exception::class)
    fun cannotGetGameWithWrongAuthorization() {
        whenAuthorizedWrong().get(gameUrl)
                .then().statusCode(SC_UNAUTHORIZED)
    }

    @Test
    @Throws(Exception::class)
    fun cannotGetJoinCodesWithoutAuthorization() {
        `when`().get(gameUrl!! + "/joinCodes")
                .then().statusCode(SC_BAD_REQUEST)
    }

    @Test
    @Throws(Exception::class)
    fun cannotGetJoinCodesWithWrongAuthorization() {
        whenAuthorizedWrong().get(gameUrl!! + "/joinCodes")
                .then().statusCode(SC_UNAUTHORIZED)
    }

    @Test
    @Throws(Exception::class)
    fun cannotCreateJoinCodesWithoutAuthorization() {
        `when`().post(gameUrl!! + "/joinCodes")
                .then().statusCode(SC_BAD_REQUEST)
    }

    @Test
    @Throws(Exception::class)
    fun cannotCreateJoinCodesWithWrongAuthorization() {
        whenAuthorizedWrong().post(gameUrl!! + "/joinCodes")
                .then().statusCode(SC_UNAUTHORIZED)
    }

    @Test
    @Throws(Exception::class)
    fun canJoinGameViaValidCode() {
        val response = `when`("{\"playerName\": \"test-player\"}").post("/api/games")

        gameUrl = response.getHeader("Location")
        gameAuthToken = response.body.jsonPath().getString("fullAuthToken")

        whenAuthorized().post(gameUrl!! + "/joinCodes")
                .then().statusCode(SC_CREATED)

        val codesResponse = whenAuthorized().get(gameUrl!! + "/joinCodes")
        assertThat(codesResponse.statusCode, `is`(SC_OK))

        val joinCodes = codesResponse.body.jsonPath().getList<String>("joinCodes")
        assertThat(joinCodes, hasSize(1))

        val joinResponse = `when`("{\"playerName\":\"new-test-player\", \"joinCode\": \"" + joinCodes[0] + "\"}")
                .post("/api/games")

        gameUrl = joinResponse.getHeader("Location")
        gameAuthToken = joinResponse.body.jsonPath().getString("fullAuthToken")

        assertThat(gameUrl, startsWith("http://localhost"))
        assertThat(gameUrl, containsString("/api/games/"))
        assertThat(gameAuthToken, `is`(notNullValue()))
        assertThat(joinResponse.statusCode, `is`(SC_CREATED))
    }

    @Test
    @Throws(Exception::class)
    fun cannotJoinGameWithDuplicatePlayerName() {
        val response = `when`("{\"playerName\": \"test-player\"}").post("/api/games")

        gameUrl = response.getHeader("Location")
        gameAuthToken = response.body.jsonPath().getString("fullAuthToken")

        whenAuthorized().post(gameUrl!! + "/joinCodes")
                .then().statusCode(SC_CREATED)

        val codesResponse = whenAuthorized().get(gameUrl!! + "/joinCodes")
        assertThat(codesResponse.statusCode, `is`(SC_OK))

        val joinCodes = codesResponse.body.jsonPath().getList<String>("joinCodes")
        assertThat(joinCodes, hasSize(1))

        `when`("{\"playerName\":\"test-player\", \"joinCode\": \"" + joinCodes[0] + "\"}")
                .post("/api/games")
                .then().statusCode(SC_CONFLICT)
    }

    @Test
    @Throws(Exception::class)
    fun cannotJoinGameViaInvalidCode() {
        `when`("{\"playerName\": \"test-player-2\", \"joinCode\": \"invalidJoinCode\"}").post("/api/games")
                .then().statusCode(SC_NOT_FOUND)
                .and().body("error", `is`("invalidjoincode is an invalid code"))
    }

    @Test
    @Throws(Exception::class)
    fun universeIsReturned() {
        whenAuthorized().get(gameUrl)
                .then().statusCode(SC_OK)
                .and().body("universe", `is`(notNullValue()))
    }

    @Test
    @Throws(Exception::class)
    fun cannotEndTurnWithoutAuthorization() {
        `when`().post(gameUrl!! + "/turns")
                .then().statusCode(SC_BAD_REQUEST)
    }

    @Test
    @Throws(Exception::class)
    fun canEndTurnWithAuthorization() {
        whenAuthorized().post(gameUrl!! + "/turns")
                .then().statusCode(SC_ACCEPTED)
    }

    @Test
    @Throws(Exception::class)
    fun cannotSendShipsWithoutAuthorization() {
        val body = String.format(SEND_SHIPS_REQUEST_BODY, 1, 1, 2)
        `when`(body).post(gameUrl!! + "/universe/travellingShipFormations")
                .then().statusCode(SC_BAD_REQUEST)
    }

    @Test
    @Throws(Exception::class)
    fun canSendShipsWithAuthorization() {
        val response = whenAuthorized().get(gameUrl)
        val homePlanetId = response.body.jsonPath().getInt("universe.homePlanet.id")
        val otherPlanetId = (homePlanetId + 1) % 20

        val body = String.format(SEND_SHIPS_REQUEST_BODY, 1, homePlanetId, otherPlanetId)
        whenAuthorized(body).post(gameUrl!! + "/universe/travellingShipFormations")
                .then().statusCode(SC_ACCEPTED)
    }

    @Test
    @Throws(Exception::class)
    fun cannotSendShipsFromWrongPlanet() {
        val response = whenAuthorized().get(gameUrl)
        val homePlanetId = response.body.jsonPath().getInt("universe.homePlanet.id")
        val otherPlanetId = (homePlanetId + 1) % 20

        val body = String.format(SEND_SHIPS_REQUEST_BODY, 1, otherPlanetId, homePlanetId)
        whenAuthorized(body).post(gameUrl!! + "/universe/travellingShipFormations")
                .then().statusCode(SC_CONFLICT)
    }

    @Test
    @Throws(Exception::class)
    fun cannotBuildFactoryWithoutAuthorization() {
        `when`().post(gameUrl!! + "/universe/planets/1/factories")
                .then().statusCode(SC_BAD_REQUEST)
    }

    @Test
    @Throws(Exception::class)
    fun canBuildFactoryWithAuthorization() {
        val response = whenAuthorized().get(gameUrl)
        val homePlanetId = response.body.jsonPath().getInt("universe.homePlanet.id")

        whenAuthorized().post("$gameUrl/universe/planets/$homePlanetId/factories")
                .then().statusCode(SC_ACCEPTED)
    }

    @Test
    @Throws(Exception::class)
    fun cannotBuildFactoryOnWrongPlanet() {
        val response = whenAuthorized().get(gameUrl)
        val homePlanetId = response.body.jsonPath().getInt("universe.homePlanet.id")
        val otherPlanetId = (homePlanetId + 1) % 20

        whenAuthorized().post("$gameUrl/universe/planets/$otherPlanetId/factories")
                .then().statusCode(SC_CONFLICT)
    }

    @Test
    @Throws(Exception::class)
    fun cannotChangeProductionFocusWithoutAuthorization() {
        `when`("{\"productionFocus\":1}").post(gameUrl!! + "/universe/planets/1")
                .then().statusCode(SC_BAD_REQUEST)
    }

    @Test
    @Throws(Exception::class)
    fun canChangeProductionFocusWithAuthorization() {
        val response = whenAuthorized().get(gameUrl)
        val homePlanetId = response.body.jsonPath().getInt("universe.homePlanet.id")

        whenAuthorized("{\"productionFocus\":1}").post("$gameUrl/universe/planets/$homePlanetId")
                .then().statusCode(SC_ACCEPTED)
    }

    @Test
    @Throws(Exception::class)
    fun cannotChangeProductionFocusOnWrongPlanet() {
        val response = whenAuthorized().get(gameUrl)
        val homePlanetId = response.body.jsonPath().getInt("universe.homePlanet.id")
        val otherPlanetId = (homePlanetId + 1) % 20

        whenAuthorized("{\"productionFocus\":1}").post("$gameUrl/universe/planets/$otherPlanetId")
                .then().statusCode(SC_CONFLICT)
    }

    @Test
    @Throws(Exception::class)
    fun cannotQuitGameWithoutAuthorization() {
        `when`().delete(gameUrl)
                .then().statusCode(SC_BAD_REQUEST)
    }

    @Test
    @Throws(Exception::class)
    fun canQuitGameWithAuthorization() {
        whenAuthorized().delete(gameUrl)
                .then().statusCode(SC_ACCEPTED)
    }

    @Test
    @Throws(Exception::class)
    fun quittingGameInvalidatesToken() {
        whenAuthorized().delete(gameUrl)
                .then().statusCode(SC_ACCEPTED)

        whenAuthorized().get(gameUrl)
                .then().statusCode(SC_UNAUTHORIZED)
        whenAuthorized().post(gameUrl!! + "/turns")
                .then().statusCode(SC_UNAUTHORIZED)
        whenAuthorized().post(gameUrl!! + "/universe/travellingShipFormations/1/1/2")
                .then().statusCode(SC_UNAUTHORIZED)
        whenAuthorized().delete(gameUrl)
                .then().statusCode(SC_UNAUTHORIZED)
    }

    private fun `when`(jsonBody: String? = null): RequestSpecification {
        var spec = given().port(SERVER_PORT).contentType(ContentType.JSON)
        if (jsonBody != null) {
            spec = spec.body(jsonBody)
        }
        return spec.`when`()
    }

    private fun whenAuthorizedWrong(): RequestSpecification {
        return `when`().header("Authorization", "Bearer " + gameAuthToken + "1")
    }

    private fun whenAuthorized(jsonBody: String? = null): RequestSpecification {
        return `when`(jsonBody).header("Authorization", "Bearer " + gameAuthToken!!)
    }

    companion object {

        private const val SEND_SHIPS_REQUEST_BODY = "{\"shipCount\": %d, \"originPlanetId\": %d, \"destinationPlanetId\": %d}"
        private const val SERVER_PORT = 8080
    }

}
