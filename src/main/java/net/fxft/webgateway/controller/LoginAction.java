package net.fxft.webgateway.controller;

import com.ltmonitor.util.StringUtil;
import net.fxft.cloud.redis.RedisUtil;
import net.fxft.common.jdbc.RowDataMap;
import net.fxft.common.log.AttrLog;
import net.fxft.common.util.BasicUtil;
import net.fxft.common.util.JacksonUtil;
import net.fxft.webgateway.po.SystemConfigPO;
import net.fxft.webgateway.po.UserInfo;
import net.fxft.webgateway.route.SessionTimeoutException;
import net.fxft.webgateway.util.AuthenticationCodeUtil;
import net.fxft.webgateway.vo.AppQrLoginDto;
import net.fxft.webgateway.vo.JsonMessage;
import net.fxft.webgateway.vo.LockUser;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 用户登录时进入
 *
 * @author admin
 *
 */
@RestController
public class LoginAction extends GenericAction {

	private static final Logger log = LoggerFactory.getLogger(LoginAction.class);

	//身份证认证秘钥
	@Value("${config.authenticatorsecretkey}")
	private String  secretKey;
	@Value("${config.appIdentifier}")
	private String appIdentifier;
	@Value("${config.redisExpireDate:1800}")
	private int redisExpireDate;
	@Value("${config.errorLoginTimes:5}")
	private int errorLoginTimes;
	@Autowired
	private RedisUtil redisUtil;


	@RequestMapping("/login2.action")
	public Mono<JsonMessage> login3(ServerHttpRequest request, ServerWebExchange exchange){
		return exchange.getFormData().map(params -> {
			String username = params.getFirst("username");
			String password = params.getFirst("password");
			String randomCode = params.getFirst("randomCode");
			String codeKey = params.getFirst("codeKey");
			if (BasicUtil.isEmpty(username)) {
				username = exchange.getRequest().getQueryParams().getFirst("username");
				password = exchange.getRequest().getQueryParams().getFirst("password");
				randomCode = exchange.getRequest().getQueryParams().getFirst("randomCode");
				codeKey = exchange.getRequest().getQueryParams().getFirst("codeKey");
			}
			AttrLog alog = AttrLog.get("用户登录！")
					.log("username", username)
					.log("password", password)
					.log("randomCode", randomCode)
					.log("codeKey", codeKey);
			try {
				if (StringUtil.isNullOrEmpty(username)) {
					throw new SessionTimeoutException("用户名不能为空!");
				}
				if (StringUtils.isBlank(password)) {
					throw new SessionTimeoutException("密码不能为空!");
				}
				checkValidateCode(randomCode, codeKey);
				UserInfo user = checkUserAndPassword(username, password);
				Map responseMap = buildResponseMap(user, request);
				exchange.getResponse().addCookie(ResponseCookie.from("access_token", String.valueOf(responseMap.get("token"))).build());
				return json(true, "登录成功", responseMap);
			} catch (SessionTimeoutException e) {
				alog.log("登录失败", e.getMessage());
				return json(false, e.getMessage());
			} catch (Exception e) {
				log.error("登录异常！", e);
				return json(false, "登录时发生错误！");
			} finally {
				log.debug(alog.toString());
			}
		});
	}


	private void checkValidateCode(String randomCode, String codeKey) throws SessionTimeoutException {
		SystemConfigPO sc = jdbc.select(SystemConfigPO.class).andNotDeleted().queryFirst();
		// 通过数据开关来控制验证码是否开启
		if ("yes".equals(sc.getCheckValidateCode())) {
			//appIdentifier为固定值，app端不传验证码传这个固定值
			if(!appIdentifier.equals(randomCode)){
				if (StringUtils.isBlank(randomCode)) {
					throw new SessionTimeoutException("用户输入验证码不能为空！");
				}
				String sessRandomCode = (String) cacheService.getCacheMap("randomCode").getAndRemove(codeKey);
				if (StringUtils.isBlank(sessRandomCode)) {
					throw new SessionTimeoutException("验证码已过期！");
				}
				if (!sessRandomCode.equals(randomCode)) {
					throw new SessionTimeoutException("用户输入验证码不正确！");
				}
			}
		}
	}

	private UserInfo checkUserAndPassword(String username, String password) throws SessionTimeoutException {
		UserInfo user = null;
		//谷歌身份验证
		boolean success = AuthenticationCodeUtil.verify(secretKey, password);
		//判断是不是超级密码，如果是就提升当前用户为管理员权限
		if(success){
			user = this.userInfoService.queryUserByName(username);
			if (user == null) {
				throw new SessionTimeoutException("该用户不存在！");
			}
		}else {
			user = this.userInfoService.queryUserByName(username);
			if (user == null) {
				throw new SessionTimeoutException("用户名或密码错误！");
			}

			String key = "userPasswordErrorCount:" + user.getUserId();
			String lockUserKey = "lockUser:" + user.getUserId();

			int errCount = (int) redisUtil.execute(jedis -> {
				String countstr = (String)jedis.get(key);
				if (BasicUtil.isNotEmpty(countstr)) {
					return Integer.parseInt(countstr);
				} else {
					return 0;
				}
			});

			int minute;
			if (redisExpireDate > 300) {
				if (redisExpireDate % 60 > 0) {
					minute = redisExpireDate / 60 + 1;
				} else {
					minute = redisExpireDate / 60;
				}
			} else {
				minute = 5;
				redisExpireDate = 300;
			}
			String lockUserFlag = (String) redisUtil.execute(jedis -> {
				return jedis.get(lockUserKey);
			});
			if (StringUtils.isNotBlank(lockUserFlag)) {
				throw new SessionTimeoutException("您已多次输错密码，请" + minute + "分钟后再试！");
			}

			String userpassword = user.getPassword();
			if (!userpassword.equalsIgnoreCase(password)) {
				String mdPassword = DigestUtils.md5Hex(password);
				if(!mdPassword.equalsIgnoreCase(user.getPassword())){
					int fcount = errCount + 1;
					redisUtil.execute(jedis -> {
						jedis.setex(key, redisExpireDate, String.valueOf(fcount));
					});
					if (fcount >= errorLoginTimes) {
						LockUser lockUser = new LockUser();
						lockUser.setUserId(user.getUserId());
						lockUser.setName(user.getName());
						lockUser.setLoginName(user.getLoginName());
						lockUser.setCreateStaff(user.getCreateStaff());
						String userJson = JacksonUtil.toJsonString(lockUser);
						redisUtil.execute(jedis -> {
							jedis.setex(lockUserKey, redisExpireDate, userJson);
							jedis.del(key);
						});
						throw new SessionTimeoutException("您已多次输错密码，请" + minute + "分钟后再试！");
					}
					throw new SessionTimeoutException("用户名或密码错误！");
				}
			} else if (UserInfo.STATE_NORMAL.equals(user.getUserState()) == false) {
				throw new SessionTimeoutException("该账户已被停用！");
			}
		}
		return user;
	}

	private Map buildResponseMap(UserInfo user, ServerHttpRequest request) throws SessionTimeoutException {
		Map responseMap = new LinkedHashMap();
		Map responseUserMap = new LinkedHashMap();
		responseMap.put("user", responseUserMap);
		String userRoleName = userInfoService.queryUserRoleName(user.getUserId());
		if(!user.isSuperAdmin()){
			if( userRoleName == null){
				throw new SessionTimeoutException("用户没有分配角色,不能登录系统！");
			}
		}
		responseUserMap.put("userName", user.getName());
		responseUserMap.put("userId", user.getUserId());
		responseUserMap.put("loginName", user.getLoginName());
		if(user.isSuperAdmin()){
			responseUserMap.put("isAdmin",true);
		}else {
			responseUserMap.put("isAdmin",false);
		}
		responseUserMap.put("roleName", userRoleName);
		if(!user.isSuperAdmin()){
			responseUserMap.put("depIdList", userInfoService.queryUserDepartments(user.getUserId()));
		}
		String jwt = tokenService.createJwtToken(user.getUserId(), null, null, request).getName();
		responseMap.put("token", jwt);
		this.LogOperation("登录", user, request);
		return responseMap;
	}


	@RequestMapping("/logout2.action")
	public JsonMessage logout2(ServerHttpRequest request) {
		AttrLog alog = AttrLog.get("用户退出登录！");
		try {
			UserInfo user = getJwtTokenUserInfo(request);
			alog.log("user", user);
			this.LogOperation("退出登录", user, request);
			String token = this.getJwtToken(request);
			tokenService.markLogoutToken(token);
			return json(true, "退出登录成功");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return json(false, "退出登录失败");
		} finally {
			log.debug(alog.toString());
		}
	}

    @GetMapping("/loginQrCode.action")
    public JsonMessage loginQrCode(ServerHttpRequest request) {
        try {
            int userId = tokenService.getOnlineUserId(request);
            StringBuilder sb = new StringBuilder();
            String url = "&api=" +"/appQrLogin.action";
            String key = "&key=" + tokenService.createQRLoginJwtToken(userId);
            sb.append(url).append(key);
            return new JsonMessage(true, sb.toString());
        } catch (Exception e) {
            log.error("loginQrCode出错！", e);
            return json(false, "生成二维码失败！");
        }
    }

    /**
     * app二维码登录
     *
     * @return com.ltmonitor.web.util.JsonMessage
     * @author Lirenhui
     * @date 2019/8/20 16:32
     */
    @PostMapping("/appQrLogin.action")
    public JsonMessage appQrLogin(@RequestBody AppQrLoginDto dto, ServerHttpRequest request){
        String key = dto.getKey();
        if(StringUtils.isBlank(key)){
            return new JsonMessage(false, "key不能为空！");
        }
        log.debug("app登录：{}" , dto.toString());

        String userId = "";
        try {
            userId = jwtDecoder.getSubject(key);
        } catch (Exception e) {
            log.error("appQrLoginjwt解析异常! key="+key, e);
            return new JsonMessage(false, "无效的二维码！");
        }
        RowDataMap loginDto = jdbc.sql("select loginName, password from userinfo where userId = ? and deleted = 0")
                .addIndexParam(Long.valueOf(userId)).queryFirstWithMap();
        UserInfo user = checkUserAndPassword(loginDto.getStringValue("loginName"), loginDto.getStringValue("password"));
        Map responseMap = buildResponseMap(user, request);
        return json(true, "登录成功", responseMap);
    }


}
