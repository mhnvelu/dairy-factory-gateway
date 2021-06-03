package com.spring.microservices.dairyfactorygateway.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.Duration;

@Profile("local-service-discovery")
@Configuration
public class LoadBalancedRouteConfig {
    @Bean
    public RouteLocator localHostRoutes(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder.routes()
                .route(r -> r.path("/api/v2/butter*", "/api/v2/butter/*", "/api/v2/butterUpc/*")
                        .uri("lb://dairy-factory-service"))
                .route(r -> r.path("/api/v1/customers/**").uri("lb://dairy-factory-order-service"))
                .route(r -> r.path("/api/v1/butter/*/inventory")
                        .filters(f -> f.circuitBreaker(
                                c -> c.setName("inventory-service-cb").setFallbackUri("forward:/inventory-failover")
                                        .setRouteId("inventory" +
                                                    "-failover"))).uri("lb://dairy-factory-inventory-service"))
                .route(r -> r.path("/inventory-failover/**").uri("lb://dairy-factory-inventory-failover-service"))
                .build();


    }

    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(3)).build())
                .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
                .build());
    }
}
