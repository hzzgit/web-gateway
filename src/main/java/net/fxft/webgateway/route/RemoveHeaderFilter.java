package net.fxft.webgateway.route;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component("removeHeaderFilter")
public class RemoveHeaderFilter implements GatewayFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest newrequest = exchange.getRequest().mutate().headers(hd -> {
            OnlineUserHeaderFilter.removeHeaders(hd);
        }).build();
        return chain.filter(exchange.mutate().request(newrequest).build());
    }

}
