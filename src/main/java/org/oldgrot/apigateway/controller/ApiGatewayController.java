package org.oldgrot.apigateway.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.oldgrot.apigateway.util.DiscoveryClient;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final DiscoveryClient discoveryClient;

    public ApiGatewayController(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }


    @RequestMapping(value = "/**", method = {RequestMethod.GET,
            RequestMethod.POST,
            RequestMethod.DELETE,
            RequestMethod.PUT})
    public Mono<ResponseEntity<String>> proxyRequest(HttpServletRequest request,
                                                     @RequestBody(required = false) String body) {

        String path = request.getRequestURI();
        String target = extractServiceName(path);

        return discoveryClient.discover(target)
                .flatMap(t -> webClient.method(HttpMethod.valueOf(request.getMethod()))
                        .uri(t + path)
                        .headers(headers -> Collections.list(request.getHeaderNames())
                                .forEach(name -> headers.add(name, request.getHeader(name))))
                        .bodyValue(body == null ? "" : body)
                        .exchangeToMono(resp -> resp.toEntity(String.class))
                )
                .onErrorResume(e -> Mono.just(ResponseEntity.status(502)
                        .body("Bad gateway:")));
    }

    private String extractServiceName(String path) {
        String[] parts = path.split("/");
        return parts.length > 1 ? parts[1] : "";
    }

}
