package net.fxft.webgateway.route;

import net.fxft.cloud.redis.RedisUtil;
import net.fxft.webgateway.license.LicenseValidator;
import net.fxft.webgateway.service.JwtTokenService;
import net.fxft.webgateway.service.JwtTokenService.ValidateTokenResult;
import net.fxft.webgateway.service.UserInfoCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Optional;

@Component
public class OnlineUserHeaderFilter implements GatewayFilter {

    private static final Logger log = LoggerFactory.getLogger(OnlineUserHeaderFilter.class);

    @Autowired
    private JwtTokenService tokenService;
    @Autowired
    private UserInfoCacheService userInfoService;
    @Value("${config.ssoSign}")
    private String ssoSign;
    @Autowired
    private RedisUtil redis;
    @Autowired
    private LicenseValidator licenseValidator;

    static void removeHeaders(HttpHeaders hd) {
        hd.remove("ssoSign");
        hd.remove("ssoUserId");
        hd.remove("ssoLoginName");
        hd.remove("ssoUserType");
        hd.remove("origin");
        hd.remove("Authorization");
    }


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (licenseValidator.isStopService()) {
            throw new SessionTimeoutException("License error.");
        }
        boolean wt = (boolean) exchange.getAttributes().getOrDefault(AutoChangeURIFilter.Without_Token, false);
        if (wt) {
            ServerHttpRequest request = exchange.getRequest().mutate().headers(hd -> {
                removeHeaders(hd);
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
            if (token == null) {
                Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
                log.error("缺少token！path=" + exchange.getRequest().getPath() + "; route=" + route);
                throw new SessionTimeoutException("session timeout.");
            } else {
                final ValidateTokenResult validateTokenResult = tokenService.validateAndChangeToken(token, exchange.getRequest());
                if (validateTokenResult.isChange()) {
                    exchange.getResponse().getHeaders().add("Authorization", validateTokenResult.getToken());
                    setCookieToken = true;
                }
//                final UserInfo fuser = userInfoService.getUserById(validateTokenResult.getUserId());
//                if (fuser == null) {
//                    throw new SessionTimeoutException("user not found.");
//                }
                ServerHttpRequest request = exchange.getRequest().mutate().headers(hd -> {
                    removeHeaders(hd);

                    hd.add("ssoSign", ssoSign);
                    hd.add("ssoSessionId", validateTokenResult.getSessionId());
                    hd.add("ssoUserId", validateTokenResult.getUser().getUserId().toString());
                    try {
                        hd.add("ssoLoginName", URLEncoder.encode(validateTokenResult.getUser().getLoginName(),"UTF-8") );
                    } catch (UnsupportedEncodingException e) {
                        log.error("", e);
                    }
                    hd.add("ssoUserType", validateTokenResult.getUser().getUserType());
                }).build();
                if (setCookieToken) {
                    log.debug("添加token到cookie1111！path=" + exchange.getRequest().getPath() + "; token=" + token);
                    exchange.getResponse().addCookie(ResponseCookie.from("access_token", token).path("/").build());
                }
                return chain.filter(exchange.mutate().request(request).build());
            }

        }
    }
}
