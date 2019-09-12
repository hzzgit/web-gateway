package net.fxft.webgateway.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import net.fxft.cloud.redis.RedisUtil;
import net.fxft.common.entity.NameAndValue;
import net.fxft.common.util.BasicUtil;
import net.fxft.common.util.TimeUtil;
import net.fxft.webgateway.jwt.JwtDecoder;
import net.fxft.webgateway.jwt.JwtEncoder;
import net.fxft.webgateway.route.SessionTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtTokenService {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenService.class);

    @Autowired
    private JwtEncoder jwtEncoder;
    @Autowired
    private JwtDecoder jwtDecoder;
    @Autowired
    private RedisUtil redis;
    @Autowired
    private AutoCacheService autoCacheService;

    private static final String sessionIdCacheName = "sessionId";

    public NameAndValue<String> createJwtToken(int userId, String sessionId) {
        String jwt = jwtEncoder.encodeSubject(String.valueOf(userId));
        if (sessionId == null) {
            sessionId = createSessionId();
        }
        autoCacheService.getCacheMap(sessionIdCacheName).put(jwt, sessionId, jwtEncoder.getJwtExpireMinute() * 60);
        return NameAndValue.of(jwt, sessionId);
    }

    public int getOnlineUserId(ServerHttpRequest request) throws Exception{
        String token = request.getHeaders().getFirst("Authorization");
        if (token == null) {
            throw new Exception("您还没有登录！");
        } else {
            String useridstr = jwtDecoder.getSubject(token);
            return Integer.parseInt(useridstr);
        }
    }

    public String createQRLoginJwtToken(int userId) {
        String jwt = jwtEncoder.encodeQRLoginSubject(String.valueOf(userId));
        return jwt;
    }


    public String getSessionIdByToken(String jwtToken) {
        String sid = (String) autoCacheService.getCacheMap(sessionIdCacheName).get(jwtToken);
        return sid;
    }

    public String createSessionId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public boolean isLogoutToken(String token) {
        boolean b = (Boolean) redis.execute(jd -> {
            String key = "logoutjwt:" + token;
            return jd.exists(key);
        });
        return b;
    }

    public void markLogoutToken(String token) {
        if (BasicUtil.isEmpty(token)) {
            return;
        }
        final String key = "logoutjwt:" + token;
        redis.execute(jd -> {
            jd.setex(key, jwtEncoder.getJwtExpireMinute() * 60, LocalDateTime.now().toString());
        });
        log.debug("markLogoutToken=" + token);
    }

    /**
     * 抛异常表示验证失败，返回null表示验证成功，返回新token表示换了token
     * @param token
     * @return
     * @throws SessionTimeoutException
     */
    public ValidateTokenResult validateAndChangeToken(String token, ServerHttpRequest request) throws SessionTimeoutException{
        if (isLogoutToken(token)) {
            throw new SessionTimeoutException("Token is invalid.");
        }
        try {
            ValidateTokenResult re  = new ValidateTokenResult();
            //这里先验证token是否有效
            String subject = jwtDecoder.getSubject(token);
            re.userId = Integer.parseInt(subject);
            String sessionId = getSessionIdByToken(token);
            //30分钟内替换新的token
            DecodedJWT djwt = jwtDecoder.decodedJWTWithoutVerify(token);
            Date exp = djwt.getExpiresAt();
            long changeTokenTime = jwtEncoder.getJwtExpireMinute()/2 * 60_000;
            if (sessionId == null || exp.getTime() - System.currentTimeMillis() < changeTokenTime) {
                NameAndValue<String> newToken = createJwtToken(Integer.parseInt(subject), sessionId);
                log.debug("自动换token！exp=" + TimeUtil.format(exp) + "; subject=" + subject + "; remoteAddr=" + request.getRemoteAddress() +
                        "; newtoken=" + newToken.getName() + "; sid=" + newToken.getValue() + "; path=" + request.getPath());
                re.isChange = true;
                re.token = newToken.getName();
                re.sessionId = newToken.getValue();
            } else {
                re.isChange = false;
                re.token = token;
                re.sessionId = sessionId;
            }
            return re;
        } catch (Exception e) {
            log.info("jwt解析异常！token" + token + "; requri=" + request.getPath() + "; expmsg="+BasicUtil.exceptionMsg(e));
            throw new SessionTimeoutException("session timeout.");
        }
    }


    public static class ValidateTokenResult {
        private boolean isChange;
        private String token;
        private String sessionId;
        private int userId;

        public boolean isChange() {
            return isChange;
        }

        public String getToken() {
            return token;
        }

        public String getSessionId() {
            return sessionId;
        }

        public int getUserId() {
            return userId;
        }
    }

}
