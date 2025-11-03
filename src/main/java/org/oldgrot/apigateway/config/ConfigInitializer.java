package org.oldgrot.apigateway.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.oldgrot.apigateway.dto.ServiceConfig;
import org.oldgrot.apigateway.service.ConfigClientService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConfigInitializer {

    private final ConfigClientService configClientService;

    @PostConstruct
    public void init() {
        ServiceConfig config = configClientService.fetchConfig("api-gateway");

    }
}
