package net.fxft.webgateway.jwt;

import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

/**
 *
 * @author huangLuSen
 * @date 2018年12月19日
 * 
 */
public interface JwtDecoder {

	DecodedJWT decodedJWTWithoutVerify(String token) throws Exception;

	String getSubject(String token) throws Exception;

	void updateJwtSecret(String jwtSecret);
}
