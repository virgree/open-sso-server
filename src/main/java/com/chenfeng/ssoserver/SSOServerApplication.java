package com.chenfeng.ssoserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(scanBasePackages="com.chenfeng")
public class SSOServerApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(SSOServerApplication.class, args);
    }

}
