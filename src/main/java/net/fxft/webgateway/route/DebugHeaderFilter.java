package net.fxft.webgateway.route;

import net.fxft.common.jdbc.JdbcUtil;
import net.fxft.webgateway.po.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author huanglusen
 * @date 2020-05-18
 */
@Component("debugHeaderFilter")
public class DebugHeaderFilter implements GatewayFilter {

    private static final Logger log = LoggerFactory.getLogger(DebugHeaderFilter.class);
    
    @Value("${config.ssoSign}")
    private String ssoSign;
    @Autowired
    private JdbcUtil jdbc;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest newrequest = exchange.getRequest().mutate().headers(hd -> {
            String ssoUserId = hd.getFirst("ssoUserId");
            if (ssoUserId == null) {
                ssoUserId = "1";
            }
            UserInfo userInfo = jdbc.select(UserInfo.class).whereIdEQ(ssoUserId).queryFirst();
            if (userInfo == null) {
                throw new RuntimeException("userId=" + ssoUserId + "的用户不存在！");
            }
            hd.add("ssoSign", ssoSign);
            hd.add("ssoSessionId", ssoUserId);
            hd.add("ssoUserId", ssoUserId);
            try {
                hd.add("ssoLoginName", URLEncoder.encode(userInfo.getLoginName(),"UTF-8"));
            } catch (UnsupportedEncodingException e) {
                log.error("", e);
            }
            hd.add("ssoUserType", userInfo.getUserType());
        }).build();
        return chain.filter(exchange.mutate().request(newrequest).build());
    }

}
