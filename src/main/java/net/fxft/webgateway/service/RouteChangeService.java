package net.fxft.webgateway.service;

import net.fxft.common.jdbc.ColumnSet;
import net.fxft.common.jdbc.JdbcUtil;
import net.fxft.webgateway.controller.RouteChangeController;
import net.fxft.webgateway.po.RouteChangeConfig;
import net.fxft.webgateway.route.RouteLocatorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 路由更新机制
 */
@Service
public class RouteChangeService {

    private static final Logger log = LoggerFactory.getLogger(RouteChangeService.class);
    @Autowired
    private JdbcUtil jdbc;

    //刷新路由
    @Autowired
    private RouteLocatorImpl routeLocator;


    /**
     * 新增路由信息
     *
     * @param routeChangeConfig
     */
    public void save(RouteChangeConfig routeChangeConfig) {
        jdbc.insert(routeChangeConfig)
                .insertColumn(ColumnSet.all().ifNotEmpty())
                .execute();
        routeLocator.refresRouts();
    }

    /**
     * 更新路由信息
     *
     * @param routeChangeConfig
     */
    public void update(RouteChangeConfig routeChangeConfig) {
        jdbc.update(routeChangeConfig)
                .andEQ("id", routeChangeConfig.getId())
                .updateColumn(ColumnSet.all().ifNotEmpty())
                .execute();
        routeLocator.refresRouts();
    }


    /**
     * 查看所有的路由信息
     *
     * @return
     */
    public List<RouteChangeConfig> list() {
        return jdbc.select(RouteChangeConfig.class)
                .andEQ("flag", 1)
                .query();
    }

    /**
     * 删除路由信息
     *
     * @param ids
     */
    public int delete(List<String> ids) {
        int execute = jdbc.update(RouteChangeConfig.class)
                .updateSet("flag", 0)
                .andIn("id", ids)
                .execute();
        routeLocator.refresRouts();
        return execute;
    }


}
