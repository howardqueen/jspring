package com.jspring.security.domain;

import java.util.Date;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

public class SecurityUserInfo {

	public Integer userId;
	public String realName;
	public Collection<? extends GrantedAuthority> authorities;
	public Collection<? extends SecurityMenu> menus;
	public Date lastVisit;

}
