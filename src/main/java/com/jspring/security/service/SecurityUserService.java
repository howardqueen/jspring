package com.jspring.security.service;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.GrantedAuthority;

import com.jspring.security.domain.SecurityUser;
import com.jspring.security.domain.SecurityRole;
import com.jspring.security.domain.SecurityUserRepository;

public class SecurityUserService implements UserDetailsService {

	public static final class SecurityUserDetails implements UserDetails {

		private static final long serialVersionUID = 1L;

		private final SecurityUserRepository<? extends SecurityUser> securityUserRepository;
		private final SecurityUser user;

		public SecurityUser getUser() {
			return user;
		}

		public SecurityUserDetails(SecurityUser user,
				SecurityUserRepository<? extends SecurityUser> securityUserRepository) {
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
			return true;
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

	private final SecurityUserRepository<? extends SecurityUser> securityUserRepository;

	public SecurityUserService(SecurityUserRepository<? extends SecurityUser> securityUserRepository) {
		this.securityUserRepository = securityUserRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		try {
			SecurityUser user = securityUserRepository.findByUserName(username);
			if (null == user) {
				throw new UsernameNotFoundException("Username not exists");
			}
			return new SecurityUserDetails(user, securityUserRepository);
		} catch (Exception e) {
			throw new UsernameNotFoundException(e.getClass().getName() + ":" + e.getMessage());
		}
	}

}
