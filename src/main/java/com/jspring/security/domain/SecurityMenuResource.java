package com.jspring.security.domain;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "SECURITY_MENU_RESOURCES", catalog = "security", //
		schema = "`menuId` int(11) NOT NULL,\n"//
				+ "`resourceId` int(11) NOT NULL,\n"//
				+ "PRIMARY KEY (`menuId`,`resourceId`)")
public class SecurityMenuResource {
	public static enum Columns {
		menuId, resourceId;
	}

	@Id
	public Integer menuId;
	@Id
	public Integer resourceId;
}
