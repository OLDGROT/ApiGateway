package org.oldgrot.apigateway.dto;

import lombok.Data;

@Data
public class ServiceConfig {
    private String serviceName;
    private String discoveryUrl;
    private String dbUrl;
}
