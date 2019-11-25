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
    private AutoCutPathFilter autoCutPathFilter;
    @Autowired
    private RemoveHeaderFilter removeHeaderFilter;
    @Autowired
    private OnlineUserHeaderFilter onlineUserHeaderFilter;
    @Autowired
    private AutoChangeURIFilter changeURIFilter;
    @Value("${attachementUrls.subiaoweb:}")
    private String noLoginUrls_subiaoweb;

    public static final String Base_Prefix = "/webgw";

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        String[] urlarrsubiaoweb = noLoginUrls_subiaoweb.split(",");
        //route是有序的
        RouteLocator rl = builder.routes()
                //对外接口不用登录
                .route(r -> r.path(toStringArray(urlarrsubiaoweb,
                        "/interfaceAPI/**",
                        "/alarmSearchActionAPI/**",
                        "/gpsApi/**",
                        "/transparentSendAPI/**",
                        "/vehicleActionAPI/**",
                        "/appimg/getAppQRCodeImg.action",
                        "/AppQRCodePicture/**",
                        "/globalplatformconfig/**",
                        "/platformconfig/getIpDomainPlatfromConfig.action",
                        "/platformconfig/getGlobalPlatfromConfig.action"))
                        .filters(f -> f.filter(autoCutPathFilter).filter(removeHeaderFilter))
                        .uri("lb://subiaoweb/")
                )
                .route(r -> r.path(
                        toStringArray("/getMenuTree.action",
                        "/getMainMenuTree.action",
                        "/mapRefresh.action",
                        "/getLockUser.action",
                        "/functionModel/**",
                        "/funcpriv/**",
                        "/role/**",
                        "/user/**",
                        "/dep/**",
                        "/securityapi/**",
                        "/BigscreenAction/**",
                        "/keyvalue/**",
                        "/basicData/getMenuTree.action",
                        "/basicData/funcpriv/query.action",
                        "/unLockUser.action")
                        )
                        .filters(f -> f.filter(autoCutPathFilter).filter(onlineUserHeaderFilter))
                        .uri("lb://security/")
                )
                .route(r -> r.path(toStringArray("/ccreport/**",
                        "/logisreport/**",
                        "/newreport/**",
                        "/safedriving/**"))
                        .filters(f -> f.filter(autoCutPathFilter).filter(onlineUserHeaderFilter))
                        .uri("lb://financialreportwebapi/"))
                .route(r -> r.path(toStringArray("/reportweb/**"))
                        .filters(f -> f.filter(autoCutPathFilter).filter(onlineUserHeaderFilter))
                        .uri("lb://reportweb/"))
                //gps服务
                .route(r -> r.path(toStringArray("/historyGpsInfo/**", "/track/**"))
                        .filters(f -> f.filter(autoCutPathFilter).filter(onlineUserHeaderFilter))
                        .uri("lb://gpswebapi/"))
                //视频
                .route(r -> r.path(toStringArray("/videoCommand/**", "/videoDownload/**",
                        "/videoPlayBack/**", "/videoRequest/**", "/videoResourceSearch/**"))
                        .filters(f -> f.filter(autoCutPathFilter).filter(onlineUserHeaderFilter))
                        .uri("lb://videowebapi/"))
                //实时监控
                .route(r -> r.path(toStringArray("/board/**", "/MobilerealData/**",
                        "/mobile/vehicle/getdeptreebyios.action",
                        "/mobile/vehicle/getDepTree.action",
                        "/realData/**", "/realDataweb/**",
                        "/vehicle/getDepTree.action",
                        "/vehicle/getDepTreexiamen.action",
                        "/vehicle/searchbyvehicle.action"))
                        .filters(f -> f.filter(autoCutPathFilter).filter(onlineUserHeaderFilter))
                        .uri("lb://monitorwebapi/"))
                //人脸识别
                .route(r -> r.path(toStringArray("/facerecognition/getFaceCompareResult", "/facerecognition/getAccStatus"))
                        .filters(f -> f.filter(autoCutPathFilter).filter(removeHeaderFilter))
                        .uri("lb://facerecognition/"))
                .route(r -> r.path(toStringArray("/facerecognition/**"))
                        .filters(f -> f.filter(autoCutPathFilter).filter(onlineUserHeaderFilter))
                        .uri("lb://facerecognition/"))
                .route(r -> r.path(toStringArray("/**"))
                        .filters(f -> f.filter(autoCutPathFilter).filter(onlineUserHeaderFilter))
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
        return rl;
    }

    private String[] toStringArray(String... str) {
        List<String> list = new ArrayList<>();
        for (String s : str) {
            s = s.trim();
            if (s.length() > 0) {
                list.add(s);
                list.add(Base_Prefix + s);
            }
        }
        return list.toArray(new String[0]);
    }


    private String[] toStringArray(String[] strarr, String... str) {
        List<String> list = new ArrayList<>();
        for (String s : strarr) {
            s = s.trim();
            if (s.length() > 0) {
                list.add(s);
                list.add(Base_Prefix + s);
            }
        }
        for (String s : str) {
            s = s.trim();
            if (s.length() > 0) {
                list.add(s);
                list.add(Base_Prefix + s);
            }
        }
        return list.toArray(new String[0]);
    }


}
