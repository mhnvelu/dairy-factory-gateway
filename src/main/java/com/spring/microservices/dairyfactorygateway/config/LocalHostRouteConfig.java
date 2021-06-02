package com.spring.microservices.dairyfactorygateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LocalHostRouteConfig {
    @Bean
    public RouteLocator localHostRoutes(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder.routes()
                .route(r -> r.path("/api/v2/butter*", "/api/v2/butter/*", "/api/v2/butterUpc/*").uri("http://localhost:8080"))
                .route(r -> r.path("/api/v1/customers/**").uri("http://localhost:8081"))
                .route(r -> r.path("/api/v1/butter/*/inventory").uri("http://localhost:8082"))
                .build();
    }
}
