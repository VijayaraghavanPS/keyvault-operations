package com.example.keyvault;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class IdControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Container
    public static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test"); // Correct driver class

    private static WireMockServer wireMockServer;

    @BeforeAll
    public static void setup() throws Exception {
        // Configure WireMock to use HTTPS on port 8443
        WireMockConfiguration wireMockConfig = WireMockConfiguration.wireMockConfig()
                .httpsPort(8443); // Enable HTTPS using WireMock's self-signed certificate

        wireMockServer = new WireMockServer(wireMockConfig);
        wireMockServer.start();
        configureFor("localhost", 8443);

        // Mock Azure Key Vault endpoints
        wireMockServer.stubFor(post(urlPathEqualTo("/keys/my-key/encrypt"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"result\":\"encryptedData\"}")));

        wireMockServer.stubFor(post(urlPathEqualTo("/keys/my-key/decrypt"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"result\":\"decryptedValue\"}")));
        RestAssured.useRelaxedHTTPSValidation();
        SSLUtilities.disableSSLValidation();
    }

    @AfterAll
    public static void teardown() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @Test
    public void testStoreIdEndpoint() {
        String requestPayload = "{\"id\": \"123\", \"value\": \"testValue\"}";

        given()
                .contentType(ContentType.JSON)
                .body(requestPayload)
                .when()
                .post("http://localhost:" + port + "/api/store")
                .then()
                .statusCode(200)
                .body(equalTo("1")); // Adjust as needed based on expected response
    }

    @Test
    public void testRetrieveIdEndpoint() {
        Long id = 1L;

        given()
                .queryParam("id", id)
                .when()
                .get("http://localhost:" + port + "/api/retrieve")
                .then()
                .statusCode(200)
                .body(equalTo("decryptedValue")); // Validate the mock decryption result
    }
}