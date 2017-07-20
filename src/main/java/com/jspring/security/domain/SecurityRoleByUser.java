package com.jspring.security.domain;

import com.jspring.persistence.JColumn;
import com.jspring.persistence.JTable;
import com.jspring.persistence.JoinTable;

@JTable(name = "SECURITY_ROLES", database = "security")
@JoinTable(name = "SECURITY_USER_ROLES", shortName = "r", column = "roleId", leftColumn = "roleId")
public class SecurityRoleByUser extends SecurityRole {

	private static final long serialVersionUID = 1L;

	public static enum Columns {
		roleId, roleName, nickName, userId;
	}

	@JColumn(table = "r", name = "userId")
	public Long userId;

}
