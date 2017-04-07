package com.jspring.security.domain;

import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;

@Table(name = "cms_roles")
public class SecurityRole implements GrantedAuthority {

	private static final long serialVersionUID = 8836282492025026777L;

	@Id
	public Integer roleId;
	public String roleName;

	public String getAuthority() {
		return roleName;
	}

}
