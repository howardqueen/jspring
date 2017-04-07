package com.jspring.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.jspring.security.service.SecurityFilter;

@Configuration
@EnableWebSecurity
@ComponentScan(value = { "com.jspring.security.domain", "com.jspring.security.service",
		"com.jspring.security.web" })
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

	@Autowired
	SecurityFilter securityFilter;

	private UserDetailsService securityUserService;

	protected UserDetailsService getUserDetailsService() {
		if (null == securityUserService) {
			securityUserService = (UserDetailsService) this.getApplicationContext().getBean("securityUserService");
		}
		return securityUserService;
	}

	public static final String[] SKIP_URLS = { "/favicon.ico", "/easyui/**", "/js/**", "/css/**" };

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()

				.antMatchers(SKIP_URLS).permitAll()

				.anyRequest().authenticated()

				.and().formLogin().loginPage("/login").permitAll()

				.and().logout().permitAll()

				.and().addFilterAfter(securityFilter, FilterSecurityInterceptor.class)

				.csrf().disable();
	}

	private static final PasswordEncoder encoder = new MyPasswordEncoder();

	private static final class MyPasswordEncoder implements PasswordEncoder {
		private final String SITE_WIDE_SECRET = "dc3949ba59abbe56e057f20f";//盐值
		private final PasswordEncoder encoder = new StandardPasswordEncoder(SITE_WIDE_SECRET);

		public String encode(CharSequence pwd) {
			if (pwd.toString() == "e10adc3949ba59abbe56e057f20f883e") {//初始密码123456
				return pwd.toString();
			}
			return encoder.encode(pwd);
		}

		public boolean matches(CharSequence pwd1, String pwd2) {
			try {
				if (pwd1.toString().equals("e10adc3949ba59abbe56e057f20f883e")
						&& pwd2.equals("e10adc3949ba59abbe56e057f20f883e")) {
					return true;
				}
				return encoder.matches(pwd1, pwd2);
			} catch (Exception e) {
				log.error(e.getClass().getName() + ":" + e.getMessage() + "\r\n" + pwd1 + "<=>" + pwd2);
				return false;
			}
		}

	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(getUserDetailsService()).passwordEncoder(encoder);
	}

}
