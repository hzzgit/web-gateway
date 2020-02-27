package net.fxft.webgateway.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * @ClassName IpUtil
 * @Author zwj
 * @Description ip
 * @Date 2020/2/17 13:48
 */
@Slf4j
public class IpUtil {

    public static String getIpAddress(ServerHttpRequest request) {
        request.getRemoteAddress();
        HttpHeaders headers = request.getHeaders();
        String ip = headers.getFirst("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("Proxy-Client-IP");

        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("WL-Proxy-Client-IP");

        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("HTTP_CLIENT_IP");

        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("HTTP_X_FORWARDED_FOR");

        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            InetSocketAddress remoteAddress = request.getRemoteAddress();
            ip = remoteAddress.getAddress().getHostAddress();
            if("127.0.0.1".equals(ip)||"0:0:0:0:0:0:0:1".equals(ip)){
                //根据网卡取本机配置的IP
                InetAddress inet=null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ip= inet.getHostAddress();
            }
        }
        return ip;
    }
}
