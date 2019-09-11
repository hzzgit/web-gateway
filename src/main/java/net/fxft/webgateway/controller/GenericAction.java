package net.fxft.webgateway.controller;

import net.fxft.cloud.redis.RedisUtil;
import net.fxft.common.jdbc.JdbcUtil;
import net.fxft.webgateway.jwt.JwtDecoder;
import net.fxft.webgateway.po.OperationLog;
import net.fxft.webgateway.po.UserInfo;
import net.fxft.webgateway.service.AutoCacheService;
import net.fxft.webgateway.service.JwtTokenService;
import net.fxft.webgateway.service.UserInfoCacheService;
import net.fxft.webgateway.vo.JsonMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 基类
 * 主要提供了Session存取在线用户的方法
 *
 * @author admin
 */
public class GenericAction {

    private static final Logger logger = LoggerFactory.getLogger(GenericAction.class);

    /**
     * 保存用户选项的key
     */
    protected static String SESSION_KEY_USER_PREFERENCE = "userPreference";

    @Autowired
    protected AutoCacheService cacheService;
    @Autowired
    protected JdbcUtil jdbc;
    @Autowired
    protected JwtDecoder jwtDecoder;
    @Autowired
    protected RedisUtil redis;
//    @Autowired
//    protected ServerHttpRequest request;
    @Autowired
    protected UserInfoCacheService userInfoService;
    @Autowired
    protected JwtTokenService tokenService;

    // Json跳转
    protected JsonMessage json(boolean success, Object data) {
        return new JsonMessage(success, data);
    }

    protected JsonMessage json(boolean success, String msg, Object data) {
        return new JsonMessage(success, msg, data);
    }

    protected JsonMessage json(boolean success, long total, Object data) {
        return new JsonMessage(success, total, data);
    }

    protected JsonMessage json(boolean success, int code, String msg, Object data) {
        return new JsonMessage(success, code, msg, data);
    }

    // Json跳转
    protected JsonMessage json(boolean success, String msg) {
        return (new JsonMessage(success, msg));
    }

    public static JsonMessage jsonSuccess(String msg) {
        return new JsonMessage(true, msg);
    }

    public static JsonMessage jsonFail(String msg) {
        return new JsonMessage(false, msg);
    }

    public String getJwtToken(ServerHttpRequest request) {
        String token = request.getHeaders().getFirst("Authorization");
        if (token == null) {
            return null;
        }else {
            if (tokenService.isLogoutToken(token)) {
                logger.debug("token已经注销！token=" + token);
                return null;
            } else {
                return token;
            }
        }
    }

    public UserInfo getJwtTokenUserInfo(ServerHttpRequest request) {
        String token = getJwtToken(request);
        if (token != null) {
            try {
                String ustr = jwtDecoder.getSubject(token);
                int userId = Integer.valueOf(ustr);
                return userInfoService.getUserById(userId);
            } catch (Exception e) {
                logger.error("解析jwtToken出错！token=" + token, e);
            }
        }
        return null;
    }

    /**
     * 记录用户的操作日志
     *
     * @param detail 操作内容描述
     */
    public void LogOperation(String detail, UserInfo u, ServerHttpRequest request) {
        try {
            if (u != null) {
                OperationLog log = new OperationLog();
                log.setDeleted(false);
                log.setUserId(u.getUserId());
                log.setUserName(u.getName());
                log.setDetail(detail);
                log.setChannel(0);
                log.setCompanyId(0L);
                String uri = request.getURI().toString();
                log.setUrl(uri);
                String Agent = request.getHeaders().getFirst("User-Agent");
                log.setIp(request.getRemoteAddress() + "[" + Agent + "]");
                jdbc.insert(log).execute();
            } else {
                logger.debug("UserInfo为null，不记录LogOperation！detail=" + detail);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

}
