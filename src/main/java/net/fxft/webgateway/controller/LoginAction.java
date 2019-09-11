package net.fxft.webgateway.controller;

import com.ltmonitor.util.StringUtil;
import net.fxft.common.log.AttrLog;
import net.fxft.webgateway.po.SystemConfigPO;
import net.fxft.webgateway.po.UserInfo;
import net.fxft.webgateway.util.AuthenticationCodeUtil;
import net.fxft.webgateway.util.ObjectFieldEmpty;
import net.fxft.webgateway.vo.AppQrLoginDto;
import net.fxft.webgateway.vo.JsonMessage;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
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
@Controller
public class LoginAction extends GenericAction {

	private static final Logger log = LoggerFactory.getLogger(LoginAction.class);

	//身份证认证秘钥
	@Value("${config.authenticatorsecretkey}")
	private String  secretKey;
	@Value("${config.appIdentifier}")
	private String appIdentifier;


	@ResponseBody
	@RequestMapping("/login2.action")
	public Mono<JsonMessage> login3(ServerHttpRequest request, ServerWebExchange exchange){
		return exchange.getFormData().map(params -> {
			String username = params.getFirst("username");
			String password = params.getFirst("password");
			String randomCode = params.getFirst("randomCode");
			String codeKey = params.getFirst("codeKey");
			AttrLog alog = AttrLog.get("用户登录！")
					.log("username", username)
					.log("password", password)
					.log("randomCode", randomCode)
					.log("codeKey", codeKey);
			log.debug(alog.toString());

			if (StringUtil.isNullOrEmpty(username)) {
				return json(false, "用户名不能为空!");
			}
			if (StringUtils.isBlank(password)) {
				return json(false, "密码不能为空!");
			}
			try {
				SystemConfigPO sc = jdbc.select(SystemConfigPO.class).andNotDeleted().queryFirst();

				// 通过数据开关来控制验证码是否开启
				if ("yes".equals(sc.getCheckValidateCode())) {
					//appIdentifier为固定值，app端不传验证码传这个固定值
					if(!appIdentifier.equals(randomCode)){
						if (StringUtils.isBlank(randomCode)) {
							return new JsonMessage(false, "用户输入验证码不能为空！");
						}

						String sessRandomCode = (String) cacheService.getCacheMap("randomCode").getAndRemove(codeKey);

						if (StringUtils.isBlank(sessRandomCode)) {
							return new JsonMessage(false, "验证码已过期");
						}

						if (!sessRandomCode.equals(randomCode)) {
							return new JsonMessage(false, "用户输入验证码不正确！");
						}
					}
				}
				UserInfo user = null;

				//谷歌身份验证
				boolean success = AuthenticationCodeUtil.verify(secretKey, password);

				//判断是不是超级密码，如果是就提升当前用户为管理员权限
				if(success){
					user = this.userInfoService.getUserByName(username);
					if (user == null) {
						return json(false, "该用户不存在！");
					}
				}else {
					user = this.userInfoService.getUserByName(username);
					if (user == null) {
						return json(false, "用户名或密码错误！");
					}
					String userpassword = user.getPassword();
					if (!userpassword.equalsIgnoreCase(password)) {
						String mdPassword = DigestUtils.md5Hex(password);
						if(!mdPassword.equals(user.getPassword())){
							return json(false, "用户名或密码错误");
						}
					} else if (UserInfo.STATE_NORMAL.equals(user.getUserState()) == false) {
						return json(false, "该账户已被停用");
					}
				}

				String userRoleName = userInfoService.queryUserRoleName(user.getUserId());
				if(!user.isSuperAdmin()){
					if( userRoleName == null){
						return new JsonMessage(false,"用户没有分配角色,不能登录系统！");
					}
				}

				String jwt = tokenService.createJwtToken(user.getUserId(), null).getName();
				Map remap = new LinkedHashMap();
				remap.put("token", jwt);
				Map usermap = new LinkedHashMap();
				remap.put("user", usermap);
				usermap.put("userName", user.getName());
				usermap.put("userId", user.getUserId());
				usermap.put("loginName", user.getLoginName());
				if(user.isSuperAdmin()){
					usermap.put("isAdmin",true);
				}else {
					usermap.put("isAdmin",false);
				}
				usermap.put("roleName", userRoleName);
				if(!user.isSuperAdmin()){
					usermap.put("depIdList", userInfoService.queryUserDepartments(user.getUserId()));
				}
				this.LogOperation("登录", user, request);
				return json(true, "登录成功", remap);
			} catch (Exception e) {
				log.error("", e);
				return json(false, "登录时发生错误");
			}
		});
	}

	@ResponseBody
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





	/**
	 * app二维码登录
	 *
	 * @param [dto]
	 * @return com.ltmonitor.web.util.JsonMessage
	 * @author Lirenhui
	 * @date 2019/8/20 16:32
	 */
//	@PostMapping("/appQrLogin.action")
//	@ResponseBody
//	public JsonMessage appQrLogin(@RequestBody AppQrLoginDto dto){
//
//		try {
//			Boolean objectFieldEmpty = ObjectFieldEmpty.isObjectFieldEmpty(dto);
//			if(objectFieldEmpty){
//				return new JsonMessage(false, "参数不能全部为空！");
//			}
//		} catch (Exception e) {
//			log.error(e.getMessage(), e);
//			return new JsonMessage(false, e.toString());
//		}
//
//		if(StringUtils.isBlank(dto.getAppIdentifier())){
//			return new JsonMessage( false, "appIdentifier不能为空！");
//		}
//
//		if(!dto.getAppIdentifier().equals(appIdentifier)){
//			return new JsonMessage(false, "appIdentifier值无效!");
//		}
//
//		String loginName = dto.getLoginName();
//		if(StringUtils.isBlank(loginName)){
//			return new JsonMessage(false, "loginName不能为空！");
//		}
//
//		String password= dto.getPassword();
//		if(StringUtils.isBlank(password)){
//			return new JsonMessage(false, "password不能为空！");
//		}
//
//		String key = dto.getKey();
//		if(StringUtils.isBlank(key)){
//			return new JsonMessage(false, "key不能为空！");
//		}
//
//		String values = String.valueOf( AppQrCodeExpirationTimeCacheUtil.get(key));
//		if(StringUtils.isBlank(values)){
//			return new JsonMessage(false, "登录二维码已失效，请重新获取登录二维码");
//		}
//
//		log.debug("app登录：{}" , JSONObject.fromObject(dto).toString());
//
//		return checkLogin(loginName, password);
//	}

}
