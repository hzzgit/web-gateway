package net.fxft.webgateway.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;

/**
 *
 * @author huangLuSen
 * @date 2019年1月9日
 *
 */
@Component
@RefreshScope
public class DefaultJwtEncoder implements JwtEncoder {

	private Algorithm algorithm;
	//默认1个小时，60分钟
	@Value("${config.jwtExpireMinute:60}")
	private int jwtExpireMinute;
	@Value("${config.jwtSecret}")
	private String jwtSecret;
	
	public DefaultJwtEncoder() {
	}

	@PostConstruct
	public void init(){
		this.algorithm = Algorithm.HMAC256(jwtSecret);
	}

	@Override
	public String encodeSubject(String subject) {
		String token = JWT.create().withSubject(subject)
				.withExpiresAt(new Date(System.currentTimeMillis() + jwtExpireMinute * 60000))
				.sign(algorithm);
		return token;
	}

	public int getJwtExpireMinute() {
		return jwtExpireMinute;
	}

	public void setJwtExpireMinute(int jwtExpireMinute) {
		this.jwtExpireMinute = jwtExpireMinute;
	}

	public String getJwtSecret() {
		return jwtSecret;
	}

	public void setJwtSecret(String jwtSecret) {
		this.jwtSecret = jwtSecret;
	}
}
