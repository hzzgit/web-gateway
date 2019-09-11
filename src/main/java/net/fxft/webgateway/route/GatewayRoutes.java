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
    @Autowired
    private AutoChangeURIFilter changeURIFilter;

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        //route是有序的
        return builder.routes()
//                .route(r -> r.path("/**")
//                        .filters(f -> f.filter(changeURIFilter)
//                                .filter(onlineUserHeaderFilter)
//                        )
//                        .uri("lb://autochange")
//                )
                .route(r -> r.path(
//                        "/login2.action",
//                        "/randomPicture.action",
                        "/getMenuTree.action",
                        "/mapRefresh.action",
//                        "/platformconfig/getGlobalPlatfromConfig.action",
//                        "/appimg/getAppQRCodeImg.action",
                        "/logout2.action"
                        )
                        .uri("lb://security/")
                )
                .route(r -> r.path("/ccreport/**",
                        "/logisreport/**",
                        "/newreport/**",
                        "/safedriving/**")
                        .filters(f -> f.filter(onlineUserHeaderFilter))
                        .uri("lb://financialreportwebapi/"))
                .route(r -> r.path("/reportweb/**")
                        .filters(f -> f.filter(onlineUserHeaderFilter))
                        .uri("lb://reportweb/"))
                .route(r -> r.path("/**")
                                .filters(f -> f.filter(onlineUserHeaderFilter))
                                .uri("lb://subiaoweb/")
                )
//                .route(r -> r.path("/login/**")
//                        .filters(f -> f.stripPrefix(1).filter(changeURIFilter))
//                        .uri("lb://autochange")
////                        .uri("lb://security/")
//                )
//                .route(r -> r.path("/security/**")
//                        .filters(
//                                f -> f.stripPrefix(1)
//                                        .filter(onlineUserHeaderFilter)
//                                .uri()
//                        )
////                        .uri("lb://security/")
//                )
//                .route(r -> r.path("/**")
//                        .filters(f -> f.filter(onlineUserHeaderFilter))
//                        .uri("https://jascsold.api.jjicar.net")
//                )
                .build();
    }




}
