package com.jspring.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

import com.jspring.security.service.SecurityDecisionManager;
import com.jspring.security.service.SecurityFilter;
import com.jspring.security.service.SecurityResourceService;
import com.jspring.security.service.SecurityUserService;
import com.jspring.Strings;
import com.jspring.data.CrudRepository;
import com.jspring.data.SqlExecutor;
import com.jspring.security.domain.*;

@Configuration
@EnableWebSecurity
@ComponentScan(value = { "com.jspring.data", "com.jspring.security.web" })
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	//////////////////////////////////////////////////
	///
	//////////////////////////////////////////////////
	private SecurityFilter _securityFilter;

	protected SecurityFilter getSecurityFilter() {
		if (null == _securityFilter) {
			_securityFilter = new SecurityFilter(new SecurityDecisionManager(),
					(SecurityResourceService) this.getApplicationContext().getBean("securityResourceService"));
		}
		return _securityFilter;
	}

	private UserDetailsService _securityUserService;

	protected UserDetailsService getSecurityUserService() {
		if (null == _securityUserService) {
			_securityUserService = new SecurityUserService(
					(SecurityUserRepository<?>) this.getApplicationContext().getBean("securityUserRepository"));
		}
		return _securityUserService;
	}

	//////////////////////////////////////////////////
	/// 登录页设置
	//////////////////////////////////////////////////
	public static final String[] SKIP_URLS = { "/favicon.ico", "/easyui/**", "/js/**", "/css/**" };

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				// 允许静态资源访问
				.antMatchers(SKIP_URLS).permitAll()
				// 开启其它资源授权
				.anyRequest().authenticated()
				// 设置登陆地址
				.and().formLogin().loginPage("/login").permitAll()
				// 设置登出地址
				.and().logout().permitAll()
				// 加入自定义过滤器
				.and().addFilterAfter(getSecurityFilter(), FilterSecurityInterceptor.class)
				// 暂停防CSRF攻击
				.csrf().disable()
				// 允许在IFRAME中嵌入展示
				.headers().frameOptions().disable();
	}

	//////////////////////////////////////////////////
	/// 密码加盐处理
	//////////////////////////////////////////////////
	private static final String PASSWORD_SITE_WIDE_SECRET = "dc3949ba59abbe56e057f20f";// 盐值
	public static final String PASSWORD_123456 = "e10adc3949ba59abbe56e057f20f883e";

	protected static final class MyPasswordEncoder implements PasswordEncoder {
		private static final Logger log = LoggerFactory.getLogger(MyPasswordEncoder.class);
		private final PasswordEncoder encoder = new StandardPasswordEncoder(PASSWORD_SITE_WIDE_SECRET);

		public String encode(CharSequence pwd) {
			if (pwd.toString() == PASSWORD_123456) {// 初始密码123456
				return pwd.toString();
			}
			return encoder.encode(pwd);
		}

		public boolean matches(CharSequence pwd1, String pwd2) {
			try {
				if (pwd1.length() == 0 || Strings.isNullOrEmpty(pwd2)) {
					return false;
				}
				if (pwd1.toString().equals(PASSWORD_123456) && pwd2.equals(PASSWORD_123456)) {// 123456
					return true;
				}
				return encoder.matches(pwd1, pwd2);
			} catch (Exception e) {
				log.error(e.getClass().getName() + ":" + e.getMessage() + "\r\n" + pwd1 + "<=>" + pwd2);
				return false;
			}
		}

	}

	public static final PasswordEncoder PASSWORD_ENCODER = new MyPasswordEncoder();

	//////////////////////////////////////////////////
	/// 密码验证
	//////////////////////////////////////////////////
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(getSecurityUserService()).passwordEncoder(PASSWORD_ENCODER);
	}

	//////////////////////////////////////////////////
	/// REPOSITORY BEANS
	//////////////////////////////////////////////////
	@Autowired
	protected SqlExecutor sqlExecutor;

	@Bean
	public SecurityUserRepository<? extends SecurityUser> securityUserRepository() {
		return new SecurityUserRepository<SecurityUser>(sqlExecutor, SecurityUser.class);
	}

	@Bean
	public CrudRepository<SecurityUserRole> securityUserRoleRepository() {
		return new CrudRepository<SecurityUserRole>(sqlExecutor, SecurityUserRole.class);
	}

	@Bean
	public CrudRepository<SecurityRole> securityRoleRepository() {
		return new CrudRepository<SecurityRole>(sqlExecutor, SecurityRole.class);
	}

	@Bean
	public CrudRepository<SecurityRoleMenu> securityRoleMenuRepository() {
		return new CrudRepository<SecurityRoleMenu>(sqlExecutor, SecurityRoleMenu.class);
	}

	@Bean
	public CrudRepository<SecurityMenu> securityMenuRepository() {
		return new CrudRepository<SecurityMenu>(sqlExecutor, SecurityMenu.class);
	}

	@Bean
	public CrudRepository<SecurityMenuResource> securityMenuResourceRepository() {
		return new CrudRepository<SecurityMenuResource>(sqlExecutor, SecurityMenuResource.class);
	}

	@Bean
	public CrudRepository<SecurityResource> securityResourceRepository() {
		return new CrudRepository<SecurityResource>(sqlExecutor, SecurityResource.class);
	}

	@SuppressWarnings({ "unchecked" })
	@Bean
	public SecurityResourceService securityResourceService() {
		return new SecurityResourceService(
				(SecurityUserRepository<?>) this.getApplicationContext().getBean("securityUserRepository"),
				(CrudRepository<SecurityResource>) this.getApplicationContext().getBean("securityResourceRepository"));
	}

}
