package com.jspring.security.defaults;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;

import com.jspring.data.SqlExecutor;
import com.jspring.data.Where;
import com.jspring.date.DateTime;
import com.jspring.persistence.CrudRepository;
import com.jspring.security.domain.ISecurityUserDetails;
import com.jspring.security.domain.SecurityUserInfo;
import com.jspring.security.service.BaseSecurityUserService;

public class SecurityUserService extends BaseSecurityUserService {

	private final CrudRepository<SecurityUser, Integer> userRepository;

	@Autowired
	public SecurityUserService(SqlExecutor sqlExecutor) {
		super(sqlExecutor);
		this.userRepository = new CrudRepository<>(sqlExecutor, SecurityUser.class);
	}

	@Override
	public ISecurityUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		SecurityUser user = userRepository.findOne(Where.of(SecurityUser.Columns.userName).equalWith(username));
		if (null == user) {
			throw new UsernameNotFoundException("Username not exists");
		}
		return new SecurityUserDetails(user, this.loadRolesByUserId(user.userId));
	}

	@Override
	public SecurityUserInfo getUserInfo() {
		ISecurityUserDetails details = (ISecurityUserDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		SecurityUser user = userRepository.findOne(details.getUserId());
		SecurityUserInfo r = new SecurityUserInfo();
		r.userId = user.userId;
		r.lastVisit = user.lastVisit;
		r.realName = user.realName;
		r.authorities = details.getAuthorities();
		r.menus = loadMenusByUserId(details.getUserId());
		return r;
	}

	@Override
	public void changePassword(Integer userId, String newPassword) {
		SecurityUser user = new SecurityUser();
		user.userId = userId;
		user.password = newPassword;
		userRepository.updateOneSkipNull(user);
	}

	@Override
	public void loginSuccess(Integer userId) {
		SecurityUser user = new SecurityUser();
		user.userId = userId;
		user.lastVisit = DateTime.getNow().getLocalDate();
		userRepository.updateOneSkipNull(user);
	}

}
