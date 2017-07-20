package com.jspring.security.defaults;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

import com.jspring.security.domain.ISecurityUserDetails;

public class SecurityUserDetails implements ISecurityUserDetails {

	private static final long serialVersionUID = 1L;

	private Integer userId;
	private String userName;
	private String password;
	private Boolean enabled;
	private Collection<? extends GrantedAuthority> roles;

	public SecurityUserDetails(SecurityUser user, Collection<? extends GrantedAuthority> roles) {
		this.userId = user.userId;
		this.userName = user.userName;
		this.password = user.password;
		this.enabled = user.enabled;
		this.roles = roles;
	}

	@Override
	public String getUsername() {
		return userName;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public boolean isEnabled() {// 是否有效
		return enabled;
	}

	@Override
	public boolean isAccountNonLocked() {// 是否锁定
		return true;
	}

	@Override
	public boolean isAccountNonExpired() {// 是否过期
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public Integer getUserId() {
		return userId;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return roles;
	}

}