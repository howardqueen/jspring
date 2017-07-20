package com.jspring.security.service;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.jspring.security.domain.SecurityMenu;
import com.jspring.security.domain.SecurityUserInfo;

public interface ISecurityUserService extends UserDetailsService {

	Collection<? extends GrantedAuthority> loadRolesByUserId(Integer userId);

	Collection<? extends SecurityMenu> loadMenusByUserId(Integer userId);

	void changePassword(Integer userId, String newPassword);

	PasswordEncoder getPasswordEncoder();

	String getDefaultPassword();

	SecurityUserInfo getUserInfo();

	void loginSuccess(Integer userId);

}
