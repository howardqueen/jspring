package com.jspring.security.domain;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.jspring.data.Dao;

@Repository
public class SecurityResourceRepository extends Dao<SecurityResource> {

	@Autowired
	public SecurityResourceRepository(JdbcTemplate jdbcTemplate) {
		super(jdbcTemplate, SecurityResource.class);
	}

	public List<SecurityRole> findRoles(Integer resourceId) {
		return findAll(SecurityRole.class,
				"select r.roleId, r.roleName from cms_roles r"
						//
						+ " left join cms_role_menus rm on r.roleId = rm.roleId"
						//
						+ " left join cms_menu_resources mr on rm.menuId = mr.menuId"
						//
						+ " where mr.resourceId = ?",
				resourceId);
	}
}
