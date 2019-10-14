package net.fxft.webgateway.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.Date;


/**
 *
 * @author huangLuSen
 * @date 2018年12月19日
 * 
 */
@Component
@RefreshScope
public class DefaultJwtDecoder implements JwtDecoder {
	
	private static final Logger log = LoggerFactory.getLogger(DefaultJwtDecoder.class);
	
	private JWTVerifier verifier;
	@Value("${config.jwtSecret}")
	private String jwtSecret;
	
	public DefaultJwtDecoder() {
	}

	@PostConstruct
	public void init(){
		verifier = JWT.require(Algorithm.HMAC256(jwtSecret)).build();
	}

	public DecodedJWT decodedJWTWithoutVerify(String token) throws Exception {
		DecodedJWT decode = JWT.decode(token);
		return decode;
	}

	@Override
	public String getSubject(String token) throws Exception {
		DecodedJWT jwt = verifier.verify(token);
		Date exp = jwt.getExpiresAt();
		if(exp == null || System.currentTimeMillis() > exp.getTime()) {
			throw new Exception("Token已过期！");
		}
		String sub = jwt.getSubject();
		if(StringUtils.hasText(sub)) {
			return sub.trim();
		}else {
			throw new Exception("Token解析失败！");
		}
	}

	@Override
	public void updateJwtSecret(String jwtSecret) {
		if(this.jwtSecret == null || !this.jwtSecret.equals(jwtSecret)) {
			this.jwtSecret = jwtSecret;
			this.init();
			log.info("更新jwtSecret！" + jwtSecret);
		}
	}

	public String getJwtSecret() {
		return jwtSecret;
	}

	public void setJwtSecret(String jwtSecret) {
		this.jwtSecret = jwtSecret;
	}
}
