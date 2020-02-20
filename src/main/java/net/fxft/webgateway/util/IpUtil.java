package net.fxft.webgateway.util;

import lombok.extern.slf4j.Slf4j;
import net.fxft.common.util.ByteUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.context.request.ServletWebRequest;

import java.net.InetAddress;
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
            log.debug("Ip来源X-Real-IP" + ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("Proxy-Client-IP");
            log.debug("Ip来源Proxy-Client-IP" + ip);

        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("WL-Proxy-Client-IP");
            log.debug("Ip来源WL-Proxy-Client-IP" + ip);

        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("HTTP_CLIENT_IP");
            log.debug("Ip来源HTTP_CLIENT_IP" + ip);

        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("HTTP_X_FORWARDED_FOR");
            log.debug("Ip来源HTTP_X_FORWARDED_FOR" + ip );

        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            InetAddress address = request.getRemoteAddress().getAddress();
            ip = address.getHostAddress();
            byte[] address1 = address.getAddress();
            log.debug("本地Ip：" + ip );
            log.debug("本地Ip：" + address.getCanonicalHostName() );
            log.debug("本地Ip：" + address.getHostName() );
            log.debug("本地Ip：" + ByteUtil.byteToHexStr(address1) );
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
