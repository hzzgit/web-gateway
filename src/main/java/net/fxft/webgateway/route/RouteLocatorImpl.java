package net.fxft.webgateway.route;

import com.ltmonitor.util.StringUtil;
import net.fxft.webgateway.po.WebRouteConfig;
import net.fxft.webgateway.service.RouteChangeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RouteLocatorImpl implements RouteLocator {

    private static final Logger log = LoggerFactory.getLogger(RouteLocatorImpl.class);

    @Autowired
    private ConfigurableApplicationContext applicationContext;
    @Autowired
    private AutoCutPathFilter autoCutPathFilter;
    @Autowired
    private RouteChangeService routeChangeService;

    private RouteLocatorBuilder builder;

    private Flux<Route> routeFlux = Flux.empty();

    private List<WebRouteConfig> dbRouteList = new ArrayList<>();

    public RouteLocatorImpl(RouteLocatorBuilder builder) {
        this.builder = builder;
    }

    @Override
    public Flux<Route> getRoutes() {
        log.info("---UpdateRouteFlux---" + routeFlux);
        return routeFlux;
    }

    @Autowired
    private ApplicationEventPublisher publisherAware;

    @PostConstruct
    private void init() {
        dbRouteList = routeChangeService.list();
        updateRoutes();
    }

    //更新路由信息
    @Scheduled(fixedDelay = 30000)
    public void refresRouts() {
        try {
            //更新路由信息
            List<WebRouteConfig> newlist = routeChangeService.list();
            if (dbRouteList.equals(newlist)) {
                return;
            }else {
                this.dbRouteList = newlist;
                updateRoutes();
            }
        } catch (Exception e) {
            log.error("定时更新路由信息失败{}", e);
        }
    }

    private void updateRoutes() {
        RouteLocatorBuilder.Builder routes = builder.routes();
        for (WebRouteConfig routeConfig : dbRouteList) {
            routes = makeRoutes(routes, routeConfig);
        }
        RouteLocator rl = routes.build();
        routeFlux = rl.getRoutes();
        //刷新内存路由信息
        publisherAware.publishEvent(new RefreshRoutesEvent(this));
        log.info("更新路由配置成功！");
    }

    private RouteLocatorBuilder.Builder makeRoutes(RouteLocatorBuilder.Builder routes, WebRouteConfig routeConfig) {
        RouteLocatorBuilder.Builder route = routes.route(r ->
                r.path(toStringArray(routeConfig.getPathName()))
                        .filters(f -> f.filters(toGatewayFilterList(routeConfig.getFliterName())))
                        .uri(routeConfig.getUrl())
                        .order(routeConfig.getOrders())
        );
        return route;
    }

    private String[] toStringArray(String str) {
        List<String> list = new ArrayList<>();
        List<String> psrts = Arrays.asList(str.split(","));
        for (String s : psrts) {
            s = s.trim();
            if (s.length() > 0) {
                list.add(s);
                list.add(GatewayRoutes.Base_Prefix + s);
            }
        }
        return list.toArray(new String[0]);
    }

    private List<GatewayFilter> toGatewayFilterList(String str) {
        List<GatewayFilter> list = new ArrayList<>();
        list.add(autoCutPathFilter);
        if (StringUtil.isNullOrEmpty(str)) {
            return list;
        }
        List<String> psrts = Arrays.asList(str.split(","));
        for (String s : psrts) {
            s = s.trim();
            if (s.length() > 0) {
                try {
                    GatewayFilter bean = (GatewayFilter) applicationContext.getBean(s);
                    list.add(bean);
                } catch (BeansException e) {
                    log.error("容器中没有获取到:" + s + "的实例对象", e);
                    continue;
                }
            }
        }
        return list;
    }


//    private String[] toStringArray(String[] strarr, String... str) {
//        List<String> list = new ArrayList<>();
//        for (String s : strarr) {
//            s = s.trim();
//            if (s.length() > 0) {
//                list.add(s);
//                list.add(GatewayRoutes.Base_Prefix + s);
//            }
//        }
//        for (String s : str) {
//            s = s.trim();
//            if (s.length() > 0) {
//                list.add(s);
//                list.add(GatewayRoutes.Base_Prefix + s);
//            }
//        }
//        return list.toArray(new String[0]);
//    }
//
//    private String[] toStringArray(String... str) {
//        List<String> list = new ArrayList<>();
//        for (String s : str) {
//            s = s.trim();
//            if (s.length() > 0) {
//                list.add(s);
//                list.add(GatewayRoutes.Base_Prefix + s);
//            }
//        }
//        return list.toArray(new String[0]);
//    }

}
