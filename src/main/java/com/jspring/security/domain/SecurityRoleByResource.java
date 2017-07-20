package com.jspring.security.domain;

import com.jspring.persistence.JColumn;
import com.jspring.persistence.JTable;
import com.jspring.persistence.JoinTable;

@JTable(name = "SECURITY_ROLES", database = "security")
@JoinTable(name = "SECURITY_ROLE_MENUS", shortName = "m", column = "roleId", leftColumn = "roleId")
@JoinTable(name = "SECURITY_MENU_RESOURCES", shortName = "r", column = "menuId", leftTable = "m", leftColumn = "menuId")
public class SecurityRoleByResource extends SecurityRole {

	private static final long serialVersionUID = 1L;

	public static enum Columns {
		roleId, roleName, nickName, resourceId;
	}

	@JColumn(table = "r")
	public Integer resourceId;

}
