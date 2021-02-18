package com.chenfeng.ssoserver.controller;


import com.chenfeng.ssoserver.model.User;
import com.chenfeng.ssoserver.scheduler.TokenManager;
import com.chenfeng.ssoserver.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Controller
@RequestMapping("/token")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ "application/json;charset=UTF-8"})
public class TokenController {
    private static final Logger logger = LoggerFactory.getLogger(TokenController.class);

    @ResponseBody
    @RequestMapping(value = "/validateTocken", method = RequestMethod.GET)
    public User validateTocken(String xtoken){
        if(!StringUtil.isEmpty(xtoken)){
            User u = TokenManager.validate(xtoken);
            return u;
        }
        return null;
    }

}
