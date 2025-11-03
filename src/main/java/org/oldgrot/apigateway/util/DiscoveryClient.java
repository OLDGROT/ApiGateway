package org.oldgrot.apigateway.util;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class DiscoveryClient {
    private final String discoveryUrl = "http://localhost:8084/discovery-service";
    private final WebClient webClient = WebClient.create();
    private final Random random = new Random();

    public Mono<String> discover(String serviceName) {
        return webClient.get()
                .uri(discoveryUrl + "/get-serviice/" + serviceName)
                .retrieve()
                .bodyToMono(List.class)
                .map(instances -> {
                    if (instances.isEmpty()) throw new RuntimeException("No instance found");
                    Map<String, Object> instance = (Map<String, Object>) instances.get(random.nextInt(instances.size()));
                    return "http://" + instance.get("host") + ":" + instance.get("port");
                });
    }
}
