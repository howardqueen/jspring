package com.jspring.security.domain;

import java.util.List;

import com.jspring.data.CrudIntegerRepository;
import com.jspring.data.SqlExecutor;
import com.jspring.data.Where;
import com.jspring.security.domain.SecurityRole;

public class SecurityUserRepository<T extends SecurityUser> extends CrudIntegerRepository<T> {

	public SecurityUserRepository(SqlExecutor sqlExecutor, Class<T> entityClass) {
		super(sqlExecutor, entityClass);
	}

	public T findByUserName(String userName) {
		return this.findOne(Where.of(SecurityUser.Columns.userName).equalWith(userName));
	}

	public List<SecurityRole> findRoles(Long userId) {
		if (userId == 0) {
			return SecurityRole.ADMIN_ROLES;
		}
		return queryPojos(SecurityRole.class, //
				"select r.roleId, r.roleName, r.nickName from SECURITY_ROLES r"//
						+ " left join SECURITY_USER_ROLES ur on r.roleId = ur.roleId"//
						+ " where ur.userId = ?",
				userId);
	}

	public List<SecurityRole> findRoles(Integer resourceId) {
		return queryPojos(SecurityRole.class, //
				"select r.roleId, r.roleName, r.nickName from SECURITY_ROLES r" //
						+ " left join SECURITY_ROLE_MENUS rm on r.roleId = rm.roleId" //
						+ " left join SECURITY_MENU_RESOURCES mr on rm.menuId = mr.menuId" //
						+ " where mr.resourceId = ?",
				resourceId);
	}

}
