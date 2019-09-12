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
                .route(r -> r.path(
                        "/getMenuTree.action",
                        "/mapRefresh.action"
//                        "/platformconfig/getGlobalPlatfromConfig.action",
//                        "/appimg/getAppQRCodeImg.action",
                        )
                        .filters(f -> f.filter(onlineUserHeaderFilter))
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
                //gps服务
                .route(r -> r.path("/historyGpsInfo/**", "/track/**")
                        .filters(f -> f.filter(onlineUserHeaderFilter))
                        .uri("lb://gpswebapi/"))
                //视频
                .route(r -> r.path("/videoCommand/**", "/videoDownload/**",
                        "/videoPlayBack/**", "/videoRequest/**", "/videoResourceSearch/**")
                        .filters(f -> f.filter(onlineUserHeaderFilter))
                        .uri("lb://videowebapi/"))
                //实时监控
                .route(r -> r.path("/board/**", "/MobilerealData/**",
                        "/mobile/vehicle/getdeptreebyios.action",
                        "/mobile/vehicle/getDepTree.action",
                        "/realData/**", "/realDataweb/**",
                        "/vehicle/getDepTree.action",
                        "/vehicle/getDepTreexiamen.action",
                        "/vehicle/searchbyvehicle.action")
                        .filters(f -> f.filter(onlineUserHeaderFilter))
                        .uri("lb://monitorwebapi/"))
                //对外接口不用登录
                .route(r -> r.path("/interfaceAPI",
                        "/alarmSearchActionAPI",
                        "/gpsApi",
                        "/transparentSendAPI",
                        "/vehicleActionAPI")
                        .uri("lb://subiaoweb/")
                )
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
