package com.jspring.security.domain;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "SECURITY_USER_ROLES", catalog = "security", //
		schema = "`userId` bigint(20) NOT NULL,\n"//
				+ "`roleId` int(11) NOT NULL,\n"//
				+ "PRIMARY KEY (`userId`,`roleId`)")
public class SecurityUserRole {

	public static enum Columns {
		userId, roleId;
	}

	@Id
	public Long userId;
	@Id
	public Integer roleId;
}
