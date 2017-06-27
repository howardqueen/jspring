package com.jspring.security.domain;

import java.util.List;

import com.jspring.data.CrudRepository;
import com.jspring.data.DaoWhere;
import com.jspring.data.DaoWhere.Operators;
import com.jspring.security.domain.SecurityRole;

public class SecurityUserDao<T extends SecurityUser> extends CrudRepository<T> {

	// public SecurityUserDao(DataManager dataManager, Class<T> clsOfT) {
	// super(dataManager, clsOfT);
	// }

	public T findByUserName(String userName) {
		return this.findOne(new DaoWhere(SecurityUser.Columns.userName, Operators.Equal, userName));
	}

	public List<SecurityRole> findRoles(Long userId) {
		if (userId == 0) {
			return SecurityRole.ADMIN_ROLES;
		}
		return getSqlExecutor().queryPojos(getMetaEntity().getDatabase(), SecurityRole.class, //
				"select r.roleId, r.roleName from SECURITY_ROLES r"//
						+ " left join SECURITY_USER_ROLES ur on r.roleId = ur.roleId"//
						+ " where ur.userId = ?",
				userId);
	}

	public List<SecurityRole> findRoles(Integer resourceId) {
		return getSqlExecutor().queryPojos(getMetaEntity().getDatabase(), SecurityRole.class,
				//
				"select r.roleId, r.roleName from SECURITY_ROLES r"
						//
						+ " left join SECURITY_ROLE_MENUS rm on r.roleId = rm.roleId"
						//
						+ " left join SECURITY_MENU_RESOURCES mr on rm.menuId = mr.menuId"
						//
						+ " where mr.resourceId = ?",
				resourceId);
	}

}
