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

    @Autowired
    private RemoveHeaderFilter removeHeaderFilter;
    @Autowired
    private OnlineUserHeaderFilter onlineUserHeaderFilter;
    @Autowired
    private AutoChangeURIFilter changeURIFilter;
    @Value("${attachementUrls.subiaoweb:}")
    private String noLoginUrls_subiaoweb;

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        String[] urlarrsubiaoweb = noLoginUrls_subiaoweb.split(",");
        //route是有序的
        return builder.routes()
                .route(r -> r.path(
                        "/getMenuTree.action",
                        "/getMainMenuTree.action",
                        "/mapRefresh.action",
                        "/getLockUser.action",
                        "/functionModel/**",
                        "/funcpriv/**",
                        "/role/**",
                        "/user/**",
                        "/dep/**",
                        "/unLockUser.action"
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
                .route(r -> r.path(toStringArray(urlarrsubiaoweb,
                        "/interfaceAPI/**",
                        "/alarmSearchActionAPI/**",
                        "/gpsApi/**",
                        "/transparentSendAPI/**",
                        "/vehicleActionAPI/**",
                        "/appimg/getAppQRCodeImg.action",
                        "/AppQRCodePicture/**",
                        "/platformconfig/getGlobalPlatfromConfig.action"))
                        .filters(f -> f.filter(removeHeaderFilter))
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



    private String[] toStringArray(String[] strarr, String ... str) {
        List<String> list = new ArrayList<>();
        for (String s : strarr) {
            s = s.trim();
            if (s.length() > 0) {
                list.add(s);
            }
        }
        for (String s : str) {
            s = s.trim();
            if (s.length() > 0) {
                list.add(s);
            }
        }
        return list.toArray(new String[0]);
    }


}
