package net.fxft.webgateway.route;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class RemoveHeaderFilter implements GatewayFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest newrequest = exchange.getRequest().mutate().headers(hd -> {
            OnlineUserHeaderFilter.removeHeaders(hd);
        }).build();
        String path = newrequest.getURI().getRawPath();
        if (path.startsWith(GatewayRoutes.Base_Prefix)) {
            String newPath = path.replaceFirst(GatewayRoutes.Base_Prefix, "");
            newrequest = newrequest.mutate().path(newPath).build();
            exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR, newrequest.getURI());
        }
        return chain.filter(exchange.mutate().request(newrequest).build());
    }

}
