package com.chenfeng.ssoserver.model;

public class LoginUser extends User {

	private String loginName;

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getLoginName() {
		return loginName;
	}

	@Override
	public String toString() {
		return loginName;
	}
}
