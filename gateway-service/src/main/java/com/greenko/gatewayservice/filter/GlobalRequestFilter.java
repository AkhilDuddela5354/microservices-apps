package com.greenko.gatewayservice.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class GlobalRequestFilter implements GlobalFilter, Ordered {

    Logger log = LoggerFactory.getLogger(GlobalRequestFilter.class);

    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        log.info("Global Filter executed");
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}