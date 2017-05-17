package com.jspring.security.domain;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "SECURITY_USER_ROLES", catalog = "security",
		//
		schema = "`userId` bigint(20) NOT NULL,\n"
				//
				+ "`roleId` int(11) NOT NULL,\n"
				//
				+ "PRIMARY KEY (`userId`,`roleId`)")
public class SecurityUserRole {
	@Id
	public Integer userId;
	@Id
	public Integer roleId;
}
