package org.oldgrot.apigateway.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;

@RestController
@RequestMapping()
public class ApiGatewayController {

    private final WebClient webClient = WebClient.create();

    @RequestMapping(value = "/**", method = {RequestMethod.GET,
            RequestMethod.POST,
            RequestMethod.DELETE,
            RequestMethod.PUT})
    public Mono<ResponseEntity<String>> proxyRequest(HttpServletRequest request,
                                                     @RequestBody(required = false) String body) {

        String path = request.getRequestURI();
        String target = route(path);

        return webClient.method(HttpMethod.valueOf(request.getMethod()))
                .uri(target)
                .headers(headers -> Collections.list(request.getHeaderNames())
                        .forEach(name -> headers.add(name, request.getHeader(name))))
                .bodyValue(body == null ? "" : body)
                .exchangeToMono(resp -> resp.toEntity(String.class));
    }

    private String route(String path) {
        if (path.startsWith("/users")) return "http://localhost:8081" + path;
        if (path.startsWith("/notifications")) return "http://localhost:8082" + path;
        return "http://localhost:8080" + path;
    }

}
