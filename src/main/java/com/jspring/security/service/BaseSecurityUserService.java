package com.jspring.security.service;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

import com.jspring.Strings;
import com.jspring.data.OrderBy;
import com.jspring.data.SqlExecutor;
import com.jspring.data.Where;
import com.jspring.persistence.Repository;
import com.jspring.security.domain.SecurityMenu;
import com.jspring.security.domain.SecurityMenuByUser;
import com.jspring.security.domain.SecurityRoleByUser;

public abstract class BaseSecurityUserService implements ISecurityUserService {

	protected final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

	private final Repository<SecurityRoleByUser, Integer> roleByUserRepository;
	private final Repository<SecurityMenuByUser, Integer> menuRepository;

	private final String defaultPassword;

	/**
	 * 
	 * @param sqlExecutor
	 * @param defaultPassword
	 *            默认密码加密后的值
	 * @param decoratePasswordSecret
	 *            密码的二次加密盐值
	 */
	public BaseSecurityUserService(SqlExecutor sqlExecutor, String defaultPassword, String decoratePasswordSecret) {
		this.roleByUserRepository = new Repository<>(sqlExecutor, SecurityRoleByUser.class);
		this.menuRepository = new Repository<>(sqlExecutor, SecurityMenuByUser.class);
		this.defaultPassword = defaultPassword;
		this.standardPasswordEncoder = new StandardPasswordEncoder(decoratePasswordSecret);
	}

	public BaseSecurityUserService(SqlExecutor sqlExecutor) {
		this(sqlExecutor, "e10adc3949ba59abbe56e057f20f883e", "dc3949ba59abbe56e057f20f");
	}

	private final PasswordEncoder standardPasswordEncoder;
	private final PasswordEncoder decoratePasswordEncoder = new PasswordEncoder() {

		public String encode(CharSequence pwd) {
			if (pwd.toString() == defaultPassword) {// 初始密码123456
				return pwd.toString();
			}
			return standardPasswordEncoder.encode(pwd);
		}

		public boolean matches(CharSequence pwd1, String pwd2) {
			try {
				if (pwd1.length() == 0 || Strings.isNullOrEmpty(pwd2)) {
					return false;
				}
				if (pwd1.toString().equals(defaultPassword) && pwd2.equals(defaultPassword)) {// 123456
					return true;
				}
				return standardPasswordEncoder.matches(pwd1, pwd2);
			} catch (Exception e) {
				log.error(e.getClass().getName() + ":" + e.getMessage() + "\r\n" + pwd1 + "<=>" + pwd2);
				return false;
			}
		}

	};

	@Override
	public PasswordEncoder getPasswordEncoder() {
		return decoratePasswordEncoder;
	}

	@Override
	public String getDefaultPassword() {
		return defaultPassword;
	}

	@Override
	public Collection<? extends SecurityMenu> loadMenusByUserId(Integer userId) {
		return menuRepository.findAllList(1, 100, //
				OrderBy.of(SecurityMenuByUser.Columns.orderWeight).desc(), //
				Where.of(SecurityMenuByUser.Columns.userId).equalWith(userId),
				Where.of(SecurityMenuByUser.Columns.parentId).greaterEqual(Integer.valueOf(0)));
	}

	@Override
	public Collection<? extends GrantedAuthority> loadRolesByUserId(Integer userId) {
		if (userId == 0) {
			return BaseSecurityResourceService.ADMIN_ROLES;
		}
		return roleByUserRepository.findAllList(1, 100, Where.of(SecurityRoleByUser.Columns.userId).equalWith(userId));
	}

}
