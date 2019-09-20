package net.fxft.webgateway.jwt;

import org.springframework.http.server.reactive.ServerHttpRequest;

/**
 *
 * @author huangLuSen
 * @date 2019年1月9日
 * 
 */
public interface JwtEncoder {

	String encodeSubject(String subject, ServerHttpRequest request);
	String encodeQRLoginSubject(String subject);
	int getJwtExpireMinute();
	
}
