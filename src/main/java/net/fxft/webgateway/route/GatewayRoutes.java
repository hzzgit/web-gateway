package net.fxft.webgateway.route;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutes {

    @Autowired
    private OnlineUserHeaderFilter onlineUserHeaderFilter;

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        //route是有序的
        return builder.routes()
                .route(r -> r.path("/login/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://security/")
                )
                .route(r -> r.path("/security/**")
                        .filters(
                                f -> f.stripPrefix(1)
                                        .filter(onlineUserHeaderFilter)
                        )
                        .uri("lb://security/")
                )
                .route(r -> r.path("/**")
                        .filters(f -> f.filter(onlineUserHeaderFilter))
                        .uri("https://jascsold.api.jjicar.net")
                )
                .build();
    }

}
