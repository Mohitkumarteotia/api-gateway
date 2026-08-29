package com.server.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class RequestLoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {

        long startTime = System.currentTimeMillis();
        String method = exchange.getRequest().getMethod().name();
        String path = exchange.getRequest().getURI().getPath();
        log.info("Incoming Request => Method: {}, Path: {}", method, path);
        return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                    long executionTime =
                            System.currentTimeMillis() - startTime;
                    int status =
                            exchange.getResponse()
                                    .getStatusCode()
                                    .value();
                    log.info("Outgoing Response => Status: {}, Time: {} ms", status, executionTime);
                }));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
