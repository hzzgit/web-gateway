package net.fxft.webgateway.route;

import com.auth0.jwt.interfaces.DecodedJWT;
import net.fxft.common.util.TimeUtil;
import net.fxft.webgateway.jwt.JwtDecoder;
import net.fxft.webgateway.jwt.JwtEncoder;
import net.fxft.webgateway.po.UserInfo;
import net.fxft.webgateway.service.UserInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.Optional;

@Component
public class OnlineUserHeaderFilter implements GatewayFilter {

    private static final Logger log = LoggerFactory.getLogger(OnlineUserHeaderFilter.class);

    @Autowired
    private JwtDecoder jwtDecoder;
    @Autowired
    private JwtEncoder jwtEncoder;
    @Autowired
    private UserInfoService userInfoService;
    @Value("${config.ssoSign}")
    private String ssoSign;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        boolean wt = (boolean) exchange.getAttributes().getOrDefault(AutoChangeURIFilter.Without_Token, false);
        if (wt) {
            ServerHttpRequest request = exchange.getRequest().mutate().headers(hd -> {
                hd.remove("ssoSign");
                hd.remove("ssoUserId");
                hd.remove("ssoLoginName");
                hd.remove("ssoUserType");
                hd.remove("origin");
            }).build();
            return chain.filter(exchange.mutate().request(request).build());
        }else {
            String token = exchange.getRequest().getHeaders().getFirst("Authorization");
            if (token == null) {
                token = exchange.getRequest().getQueryParams().getFirst("access_token");
            }
            String cookieToken = Optional.ofNullable(exchange.getRequest().getCookies().getFirst("access_token"))
                    .map(c -> c.getValue()).orElse(null);
            boolean setCookieToken = false;
            if (cookieToken == null ) {
                if (token != null) {
                    setCookieToken = true;
                }
            }else{
                if (token != null && !token.equalsIgnoreCase(cookieToken)) {
                    setCookieToken = true;
                }
            }
            if (token == null && cookieToken != null) {
                token = cookieToken;
            }
            UserInfo userinfo = null;
            if (token == null) {
                throw new SessionTimeoutException("session timeout.");
            } else {
                try {
                    String subject = jwtDecoder.getSubject(token);
                    userinfo = this.userInfoService.getLoginableUserById(Integer.valueOf(subject));

                    //30分钟内替换新的token
                    DecodedJWT djwt = jwtDecoder.decodedJWTWithoutVerify(token);
                    Date exp = djwt.getExpiresAt();
                    if (exp.getTime() - System.currentTimeMillis() < 30 * 60_000) {
                        String newToken = jwtEncoder.encodeSubject(subject);
                        token = newToken;
                        exchange.getResponse().getHeaders().add("Authorization", newToken);
                        setCookieToken = true;
                        log.debug("自动换token！exp=" + TimeUtil.format(exp) + "; subject=" + subject +
                                "; path=" + exchange.getRequest().getPath());
                    }
                } catch (Exception e) {
                    log.error("jwt解析异常！token" + token + "; requri=" + exchange.getRequest().getPath(), e);
                    throw new SessionTimeoutException("invalid jwt token.");
                }
            }
            if (userinfo == null) {
                throw new SessionTimeoutException("session timeout.");
            }
            final UserInfo fuser = userinfo;
            final String sessionId =
                    Optional.ofNullable(exchange.getRequest().getCookies().getFirst("JSESSIONID"))
                        .map(c -> c.getValue()).orElse(token);
            ServerHttpRequest request = exchange.getRequest().mutate().headers(hd -> {
                hd.remove("ssoSign");
                hd.remove("ssoUserId");
                hd.remove("ssoLoginName");
                hd.remove("ssoUserType");
                hd.remove("origin");

                hd.add("ssoSign", ssoSign);
                hd.add("ssoSessionId", sessionId);
                hd.add("ssoUserId", fuser.getUserId().toString());
                hd.add("ssoLoginName", fuser.getLoginName());
                hd.add("ssoUserType", fuser.getUserType());
            }).build();
            if (setCookieToken) {
                log.debug("添加token到cookie！path=" + exchange.getRequest().getPath() + "; token=" + token);
                exchange.getResponse().addCookie(ResponseCookie.from("access_token", token).path("/").build());
            }
            return chain.filter(exchange.mutate().request(request).build());
        }
    }
}
