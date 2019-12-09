package net.fxft.webgateway.controller;


import net.fxft.common.log.RestExecuter;
import net.fxft.webgateway.po.RouteChangeConfig;
import net.fxft.webgateway.route.RouteLocatorImpl;
import net.fxft.webgateway.service.RouteChangeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    RouteChangeService routeChangeService;


    //刷新路由信息
    @RequestMapping("/refreshroute")
    public String refreshRoute() {
        return RestExecuter.build(log, "刷新路由信息").run(alog -> {
           // routeChangeService.refres();
            return "success";
        });

    }

    //查看所有
    @PostMapping("/list")
    public String list() {
        return RestExecuter.build(log, "查看所有路由状态").run(alog -> {
            return routeChangeService.list();
        });

    }

    //新增路由信息
    @PostMapping("/addroute")
    public String addRoute(@RequestBody @Validated RouteChangeConfig routeChangeConfig) {
        return RestExecuter.build(log, "查看所有路由状态").run(alog -> {
            routeChangeService.save(routeChangeConfig);
            return "success";
        });

    }

    //更新路由信息
    @PostMapping("/updateroute")
    public String updateroute(@RequestBody @Validated RouteChangeConfig routeChangeConfig) {
        return RestExecuter.build(log, "查看所有路由状态").run(alog -> {
            routeChangeService.update(routeChangeConfig);
            return "success";
        });

    }


    //更新路由信息
    @PostMapping("/deleteroute")
    public String deleteroute(@RequestBody List<String> ids) {
        return RestExecuter.build(log, "查看所有路由状态").run(alog -> {
            int delete = routeChangeService.delete(ids);
            if (delete > 0) {
                return "success";
            }
            return "faile";
        });
    }
}
