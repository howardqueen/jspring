package com.jspring.security.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;

@Table(name = "SECURITY_ROLES", catalog = "security", //
		schema = "`roleId` int(11) NOT NULL AUTO_INCREMENT,\n"//
				+ "`roleName` varchar(50) NOT NULL,\n"//
				+ "`nickName` varchar(50) NOT NULL,\n"//
				+ "PRIMARY KEY (`roleId`)")
public class SecurityRole implements GrantedAuthority {

	public static final List<SecurityRole> ADMIN_ROLES = new ArrayList<>();
	public static final SecurityRole ADMIN = new SecurityRole();
	static {
		ADMIN.roleId = 0;
		ADMIN.roleName = "ROLE_ADMIN";
		ADMIN.nickName = "超级管理员";
		ADMIN_ROLES.add(ADMIN);
	}

	private static final long serialVersionUID = 8836282492025026777L;

	public static enum Columns {
		roleId, roleName, nickName;
	}

	@Id
	public Integer roleId;
	public String roleName;//ROLE_ADMIN
	public String nickName;

	@Override
	public String getAuthority() {
		return roleName;
	}

}
