package com.jspring.security.domain;

import com.jspring.persistence.JColumn;
import com.jspring.persistence.JTable;
import com.jspring.persistence.JoinTable;

@JTable(name = "SECURITY_MENUS", database = "security")
@JoinTable(name = "SECURITY_ROLE_MENUS", shortName = "m", column = "menuId", leftColumn = "menuId")
@JoinTable(name = "SECURITY_USER_ROLES", shortName = "r", column = "roleId", leftTable = "m", leftColumn = "roleId")
public class SecurityMenuByUser extends SecurityMenu {

	public static enum Columns {
		menuId, menuname, nickName, parentId, url, orderWeight, userId;
	}

	@JColumn(table = "r")
	public Integer userId;

}
