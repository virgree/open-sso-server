package com.chenfeng.ssoserver.service;

import javax.servlet.http.HttpSession;
import java.util.Map;

public interface CaptchaService {

    Map<String, Object> showCaptcha(HttpSession session) throws Exception;

}
