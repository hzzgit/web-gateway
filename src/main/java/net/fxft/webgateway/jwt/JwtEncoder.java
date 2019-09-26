package net.fxft.webgateway.jwt;

import net.fxft.webgateway.po.UserInfo;
import org.springframework.http.server.reactive.ServerHttpRequest;

/**
 *
 * @author huangLuSen
 * @date 2019年1月9日
 * 
 */
public interface JwtEncoder {

	String encodeSubject(UserInfo user, ServerHttpRequest request);
	String encodeQRLoginSubject(UserInfo user);
	int getJwtExpireMinute();
	UserInfo parseSubject(String subject);
	
}
