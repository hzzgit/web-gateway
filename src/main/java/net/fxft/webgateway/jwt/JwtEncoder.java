package net.fxft.webgateway.jwt;

/**
 *
 * @author huangLuSen
 * @date 2019年1月9日
 * 
 */
public interface JwtEncoder {

	String encodeSubject(String subject);
	String encodeQRLoginSubject(String subject);
	int getJwtExpireMinute();
	
}
