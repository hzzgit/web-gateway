package net.fxft.webgateway.route;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class GatewayRoutes {

    public static final String Base_Prefix = "/webgw";

    @Autowired
    private RouteLocatorImpl routeLocator;

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return routeLocator;
    }

}
