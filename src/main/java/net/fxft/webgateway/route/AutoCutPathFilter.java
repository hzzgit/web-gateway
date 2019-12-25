package net.fxft.webgateway.route;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component("autoCutPathFilter")
public class AutoCutPathFilter implements GatewayFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest newrequest = exchange.getRequest();
        String path = newrequest.getURI().getRawPath();
        if (path.startsWith(GatewayRoutes.Base_Prefix)) {
            String newPath = path.replaceFirst(GatewayRoutes.Base_Prefix, "");
            newrequest = newrequest.mutate().path(newPath).build();
            exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR, newrequest.getURI());
        }
        return chain.filter(exchange.mutate().request(newrequest).build());
    }

}
