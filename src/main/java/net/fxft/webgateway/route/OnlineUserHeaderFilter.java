package net.fxft.webgateway.route;

import net.fxft.webgateway.jwt.JwtDecoder;
import net.fxft.webgateway.po.UserInfo;
import net.fxft.webgateway.service.UserInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class OnlineUserHeaderFilter implements GatewayFilter {

    private static final Logger log = LoggerFactory.getLogger(OnlineUserHeaderFilter.class);

    @Autowired
    private JwtDecoder jwtDecoder;
    @Autowired
    private UserInfoService userInfoService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (token == null) {
            token = exchange.getRequest().getQueryParams().getFirst("access_token");
        }
        UserInfo userinfo = null;
        if (token == null) {
            throw new SessionTimeoutException("session timeout.");
        }else{
            try {
                String subject = jwtDecoder.getSubject(token);
                userinfo = this.userInfoService.getLoginableUserById(Integer.valueOf(subject));
            } catch (Exception e) {
                log.error("jwt解析异常！token" + token+"; requri="+exchange.getRequest().getPath(), e);
//                response.setHeader("Access-Control-Allow-Origin", origin);
//                response.setHeader("Access-Control-Allow-Headers", "*");
//                response.setHeader("Access-Control-Allow-Methods", "OPTIONS,GET,POST,DELETE,PUT");
//                response.setHeader("Access-Control-Allow-Credentials", "true");
                throw new SessionTimeoutException("invalid jwt token.");
            }
        }
        if (userinfo == null) {
            throw new SessionTimeoutException("session timeout.");
        }
        final UserInfo fuser = userinfo;
        ServerHttpRequest request = exchange.getRequest().mutate().headers(hd -> {
            hd.remove("ssoUserId");
            hd.remove("ssoLoginName");
            hd.remove("ssoUserType");
            hd.add( "ssoUserId", fuser.getUserId().toString());
            hd.add( "ssoLoginName", fuser.getLoginName());
            hd.add( "ssoUserType", fuser.getUserType());
        }).build();
        return chain.filter(exchange.mutate().request(request).build());
    }
}
