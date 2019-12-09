package net.fxft.webgateway.controller;


import net.fxft.common.log.RestExecuter;
import net.fxft.webgateway.route.RouteLocatorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 增删改root信息
 */
@RestController
@RequestMapping("/route")
public class RouteChangeController {
    private static final Logger log = LoggerFactory.getLogger(RouteChangeController.class);


    @Autowired
    private RouteLocatorImpl routeLocator;

    //刷新路由信息
    @RequestMapping("/refreshroute")
    public String refreshRoute() {
        return RestExecuter.build(log, "刷新路由信息").run(alog -> {
            routeLocator.refresRouts();
            return "success";
        });

    }

}
