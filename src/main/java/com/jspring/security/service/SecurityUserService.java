package com.jspring.security.service;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.jspring.security.domain.SecurityUser;
import com.jspring.security.domain.SecurityRole;
import com.jspring.security.domain.SecurityUserDao;

@Service
public class SecurityUserService<T extends SecurityUser> implements UserDetailsService {

	public static final class SecurityUserDetails<T extends SecurityUser> implements UserDetails {

		private static final long serialVersionUID = 1L;

		private final SecurityUserDao<T> securityUserRepository;
		private final T user;

		public SecurityUser getUser() {
			return user;
		}

		public SecurityUserDetails(T user, SecurityUserDao<T> securityUserRepository) {
			this.user = user;
			this.securityUserRepository = securityUserRepository;
		}

		@Override
		public String getUsername() {
			return user.userName;
		}

		@Override
		public String getPassword() {
			return user.password;
		}

		@Override
		public boolean isEnabled() {// 是否有效
			return this.user.isEnabled;
		}

		@Override
		public boolean isAccountNonLocked() {// 是否锁定
			return this.user.isLocked ? false : true;
		}

		@Override
		public boolean isAccountNonExpired() {// 是否过期
			return true;
		}

		@Override
		public boolean isCredentialsNonExpired() {
			return true;
		}

		private List<SecurityRole> roles;

		@Override
		public Collection<? extends GrantedAuthority> getAuthorities() {
			if (null == roles) {
				roles = securityUserRepository.findRoles(user.userId);
			}
			return roles;
		}

	}

	@Autowired
	SecurityUserDao<T> securityUserRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		try {
			T user = securityUserRepository.findByUserName(username);
			if (null == user) {
				throw new UsernameNotFoundException("Username not exists");
			}
			return new SecurityUserDetails<T>(user, securityUserRepository);
		} catch (Exception e) {
			throw new UsernameNotFoundException(e.getClass().getName() + ":" + e.getMessage());
		}
	}

}
