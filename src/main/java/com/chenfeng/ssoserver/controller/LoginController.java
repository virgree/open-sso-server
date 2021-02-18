package com.chenfeng.ssoserver.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.chenfeng.ssoserver.beans.AppConfigBean;
import com.chenfeng.ssoserver.scheduler.TokenManager;
import com.chenfeng.ssoserver.constants.Constants;
import com.chenfeng.ssoserver.model.Apps;
import com.chenfeng.ssoserver.model.LoginUser;
import com.chenfeng.ssoserver.model.User;
import com.chenfeng.ssoserver.service.CaptchaService;
import com.chenfeng.ssoserver.util.CookieUtil;
import com.chenfeng.ssoserver.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class LoginController {

	@Autowired
	private AppConfigBean config;

	@Autowired
	private CaptchaService captchaService;

	/**
	 * 登录入口
	 *
	 */
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(HttpServletRequest request, String backUrl,
						HttpServletResponse response, ModelMap map) throws Exception {

		String xtoken = CookieUtil.getCookie("xtoken", request);
		if (xtoken == null) {
			return "/login";
		} else {
			User loginUser = TokenManager.validate(xtoken);
			if (loginUser != null) { // token有效
				return validateSuccess(backUrl, xtoken, loginUser, response, map); // 验证成功后操作
			} else {
				return "/login";
			}
		}
	}

	@RequestMapping("/showCaptcha")
	@ResponseBody
	public Map<String, Object> showCaptcha(HttpSession session) throws Exception {

		return  captchaService.showCaptcha(session);

	}

	// token验证成功
	private String validateSuccess(String backUrl, String xtoken, User loginUser, HttpServletResponse response, ModelMap map)
			throws Exception {

		if (backUrl != null) {
			response.sendRedirect(StringUtil.appendUrlParameter(backUrl, "xtoken", xtoken));
			return null;
		} else {
			map.put("apps", config.getAuthedApps(loginUser));
			map.put("xtoken", xtoken);
			map.put("loginUser", loginUser);
			return "/login";
		}

	}

	/**
	 * 登录验证
	 *
	 * @param backUrl
	 * @param rememberMe
	 * @param request
	 * @param session
	 * @param response
	 * @param map
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/login")
	public String login(String backUrl, Boolean rememberMe, HttpServletRequest request, HttpSession session, HttpServletResponse response, ModelMap map) throws Exception {

		String captcha = request.getParameter("captcha");
		// 获取session中保存的验证码
		String sessionCaptchaValue = (String) session.getAttribute(Constants.SESSION_CAPTCHA_KEY);
		if (!captcha.equalsIgnoreCase(sessionCaptchaValue)) {
			map.put("errorMsg","验证码错误");
			return "/login";
		}
		if ("admin".equals(request.getParameter("username")) && "admin".equals(request.getParameter("passwd"))) {
			LoginUser user = new LoginUser();
			user.setLoginName("admin");
			String xtoken = authSuccess(response, user, rememberMe);
			return validateSuccess(backUrl, xtoken, user, response, map);

		} else {
			map.put("errorMsg","帐号或密码错误");
			return null;
		}
	}

	/**
	 * 用户退出
	 *
	 * @param backUrl
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/logout")
	public String logout(String backUrl, HttpServletRequest request, HttpServletResponse response) throws IOException {

		String xtoken = CookieUtil.getCookie("xtoken", request);

		// 移除token
		TokenManager.invalid(xtoken);

		// 移除server端token cookie
		Cookie cookie = new Cookie("xtoken", null);
		cookie.setMaxAge(0);
		response.addCookie(cookie);

		// 通知各客户端logout
		for (Apps clientSystem : config.getApps()) {
			clientSystem.noticeLogout(xtoken);
		}

		if (backUrl == null) {
			return "/logout";
		} else {
			response.sendRedirect(backUrl);
			return null;
		}
	}

	// 授权成功后的操作
	private String authSuccess(HttpServletResponse response,
							   User loginUser, Boolean rememberMe) {
		// 生成xtoken
		String xtoken = StringUtil.uniqueKey();
		// 存入Map
		TokenManager.addToken(xtoken, loginUser);
		// 写 Cookie
		Cookie cookie = new Cookie("xtoken", xtoken);
		response.addCookie(cookie);
		return xtoken;
	}


}