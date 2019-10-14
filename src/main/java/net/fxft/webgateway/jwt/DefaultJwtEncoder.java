package net.fxft.webgateway.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import net.fxft.webgateway.po.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import reactor.netty.http.server.HttpServerRequest;

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

	private static final Logger log = LoggerFactory.getLogger(DefaultJwtEncoder.class);
	
	private Algorithm algorithm;
	//默认1个小时，60分钟
	@Value("${config.jwtExpireMinute:60}")
	private int jwtExpireMinute;
	@Value("${config.appQRLoginJwtExpireMinute:5}")
	private int appQRLoginJwtExpireMinute;
	@Value("${config.jwtSecret}")
	private String jwtSecret;
	
	public DefaultJwtEncoder() {
	}

	@PostConstruct
	public void init(){
		this.algorithm = Algorithm.HMAC256(jwtSecret);
	}

	@Override
	public String encodeSubject(UserInfo user, ServerHttpRequest request) {
		int exptime = jwtExpireMinute * 60000;
		if (request != null) {
			String origin = request.getHeaders().getFirst("Origin");
			if (origin != null && origin.contains("yapi.fxft.net")) {
				//ios登录过期时间为1天
				log.debug("IOS登录，token有效期为1天！");
				exptime = 24 * 60 * 60000;
			}
		}
		String token = JWT.create().withSubject(createSubject(user))
				.withExpiresAt(new Date(System.currentTimeMillis() + exptime))
				.sign(algorithm);
		return token;
	}

	private String createSubject(UserInfo user) {
		String subject = user.getUserId() + "," + user.getLoginName() + "," + user.getUserType();
		return subject;
	}

	public UserInfo parseSubject(String subject) {
		String[] sarr = subject.split(",");
		if (sarr.length != 3) {
			throw new RuntimeException("subject格式不正确！subject=" + subject);
		}
		UserInfo ui = new UserInfo();
		ui.setUserId(Integer.parseInt(sarr[0]));
		ui.setLoginName(sarr[1]);
		ui.setUserType(sarr[2]);
		return ui;
	}

	@Override
	public void updateJwtSecret(String jwtSecret) {
		if(this.jwtSecret == null || !this.jwtSecret.equals(jwtSecret)) {
			this.jwtSecret = jwtSecret;
			this.init();
			log.info("更新jwtSecret！" + jwtSecret);
		}
	}

	public String encodeQRLoginSubject(UserInfo user) {
		String token = JWT.create().withSubject(createSubject(user))
				.withExpiresAt(new Date(System.currentTimeMillis() + appQRLoginJwtExpireMinute * 60000))
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
