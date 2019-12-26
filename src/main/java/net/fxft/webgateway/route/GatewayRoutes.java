package net.fxft.webgateway.route;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutes {

    public static final String Base_Prefix = "/webgw";

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return new RouteLocatorImpl(builder);
    }

}
