package com.spring.microservices.dairyfactorygateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("local-service-discovery")
@Configuration
public class LoadBalancedRouteConfig {
    @Bean
    public RouteLocator localHostRoutes(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder.routes()
                .route(r -> r.path("/api/v2/butter*", "/api/v2/butter/*", "/api/v2/butterUpc/*")
                        .uri("lb://dairy-factory-service"))
                .route(r -> r.path("/api/v1/customers/**").uri("lb://dairy-factory-order-service"))
                .route(r -> r.path("/api/v1/butter/*/inventory").uri("lb://dairy-factory-inventory-service"))
                .build();
    }
}
