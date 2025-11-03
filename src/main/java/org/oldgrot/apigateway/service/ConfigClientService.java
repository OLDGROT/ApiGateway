package org.oldgrot.apigateway.service;

import org.oldgrot.apigateway.dto.ServiceConfig;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ConfigClientService {

    private final WebClient webClient = WebClient.create("http://localhost:8085");

    public ServiceConfig fetchConfig(String serviceName) {
        return webClient.get()
                .uri("/config/" + serviceName)
                .retrieve()
                .bodyToMono(ServiceConfig.class)
                .block();
    }
}
