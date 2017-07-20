package com.jspring.security.domain;

import javax.persistence.Id;

import org.springframework.security.core.GrantedAuthority;

import com.jspring.persistence.JTable;

@JTable(name = "SECURITY_ROLES", database = "security")
public class SecurityRole implements GrantedAuthority {

	private static final long serialVersionUID = 1L;

	public static enum Columns {
		roleId, roleName, nickName;
	}

	@Id
	public Integer roleId;
	public String roleName;
	public String nickName;

	@Override
	public String getAuthority() {
		return roleName;
	}

}
