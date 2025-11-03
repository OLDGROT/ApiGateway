package org.oldgrot.apigateway;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;



@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class ApiGatewayTest {

    @Autowired
    WebTestClient webClient;

    @Test
    public void testUserRoute() {
        webClient.get()
                .uri("/users/getUsers").
                exchange()
                .expectStatus().isOk();

    }

    @Test
    public void testBadRoute() {
        webClient.get()
                .uri("/test/22").
                exchange()
                .expectStatus().isNotFound();

    }
}
