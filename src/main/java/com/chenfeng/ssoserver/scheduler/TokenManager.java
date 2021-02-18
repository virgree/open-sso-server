package com.chenfeng.ssoserver.scheduler;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import com.chenfeng.ssoserver.util.SpringContextUtil;
import com.chenfeng.ssoserver.constants.Constants;
import com.chenfeng.ssoserver.model.Apps;
import com.chenfeng.ssoserver.model.User;
import com.chenfeng.ssoserver.beans.AppConfigBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 存储token和用户的关系，可以用redis
 *
 */
public class TokenManager {
	
	private static Logger logger = LoggerFactory.getLogger(TokenManager.class); 

	private static final Timer timer = new Timer(true);
	private static final AppConfigBean config = SpringContextUtil.getBean(AppConfigBean.class);

	static {
		timer.schedule(new TimerTask() {

			@Override
			public void run() {

				for (Entry<String, Token> entry : DATA_MAP.entrySet()) {
					String xtoken = entry.getKey();
					Token token = entry.getValue();
					Date expired = token.expired;
					Date now = new Date();
					if (now.compareTo(expired) > 0) {
						//令牌自动延期
						List<Apps> clientSystems = config.getApps();
						Date maxClientExpired = expired;
						for (Apps clientSystem : clientSystems) {
							Date clientExpired = clientSystem.noticeTimeout(xtoken, Constants.TOKEN_TIMEOUT);
							if (clientExpired != null
									&& clientExpired.compareTo(now) > 0) {
								maxClientExpired = maxClientExpired.compareTo(clientExpired) < 0 ? clientExpired : maxClientExpired;
							}
						}

						if (maxClientExpired.compareTo(now) > 0) { // 客户端最大过期时间大于当前
							logger.info("更新过期时间到" + maxClientExpired);
							token.expired = maxClientExpired;
						} else {
							logger.info("清除过期token：" + xtoken);
							DATA_MAP.remove(xtoken);
						}
					}
				}
			}
		}, 60 * 1000, 60 * 1000);
	}

	private TokenManager() {
	}

	private static class Token {
		private User loginUser; // 登录用户对象
		private Date expired; // 过期时间
	}

	private static final Map<String, Token> DATA_MAP = new ConcurrentHashMap<String, Token>();

	/**
	 * 验证Token有效性
	 * 
	 * @param xtoken
	 * @return
	 */
	public static User validate(String xtoken) {
		Token token = DATA_MAP.get(xtoken);
		return token == null ? null : token.loginUser; 
	}

	/**
	 * 用户授权成功后将授权信息存入
	 * 
	 * @param xtoken
	 * @param loginUser
	 */
	public static void addToken(String xtoken, User loginUser) {
		Token token = new Token();
		token.loginUser = loginUser;
		token.expired = new Date(new Date().getTime() + Constants.TOKEN_TIMEOUT * 60 * 1000);
		DATA_MAP.put(xtoken, token);
	}
	
	public static void invalid(String xtoken) {
		if (xtoken != null) {
			DATA_MAP.remove(xtoken);
		}
	}
}
