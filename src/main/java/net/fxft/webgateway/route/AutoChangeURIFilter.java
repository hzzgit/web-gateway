package net.fxft.webgateway.route;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
public class AutoChangeURIFilter implements GatewayFilter {

    public static final String Without_Token = "Without_Token";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String sname = exchange.getRequest().getPath().subPath(1, 2).toString();
        if (sname.endsWith("2")) {
            sname = sname.substring(0, sname.length() - 1);
            exchange.getAttributes().put(Without_Token, true);
        }

        URI requestURL = UriComponentsBuilder.fromUriString("lb://"+sname).build().toUri();
        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        Route newroute = Route.async()
                .asyncPredicate(route.getPredicate())
                .filters(route.getFilters())
                .id(route.getId())
                .order(route.getOrder())
                .uri(requestURL).build();
        exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR, newroute);

        ServerHttpRequest request = exchange.getRequest();
        ServerWebExchangeUtils.addOriginalRequestUrl(exchange, request.getURI());
        String newPath = exchange.getRequest().getPath().subPath(2).toString();
        ServerHttpRequest newRequest = request.mutate().path(newPath).build();
        exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR, newRequest.getURI());
        return chain.filter(exchange.mutate().request(newRequest).build());
    }
}
