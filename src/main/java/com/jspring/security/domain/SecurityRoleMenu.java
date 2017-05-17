package com.jspring.security.domain;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "SECURITY_ROLE_MENUS", catalog = "security",
		//
		schema = "`roleId` int(11) NOT NULL,\n"
				//
				+ "`menuId` int(11) NOT NULL,\n"
				//
				+ "PRIMARY KEY (`roleId`,`menuId`)")
public class SecurityRoleMenu {
	@Id
	public Integer roleId;
	@Id
	public Integer menuId;
}
