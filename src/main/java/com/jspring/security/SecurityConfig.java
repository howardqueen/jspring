package com.jspring.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import com.jspring.security.defaults.SecurityUserService;
import com.jspring.security.domain.ISecurityUserDetails;
import com.jspring.security.service.ISecurityResourceService;
import com.jspring.security.service.ISecurityUserService;
import com.jspring.security.service.SecurityFilter;
import com.jspring.security.service.BlackSecurityResourceService;
import com.jspring.security.service.WhiteSecurityResourceService;
import com.jspring.Exceptions;
import com.jspring.Strings;
import com.jspring.data.SqlExecutor;

@Configuration
@EnableWebSecurity
@ComponentScan(value = { "com.jspring.data", "com.jspring.security.service", "com.jspring.security.web" })
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class.getSimpleName());

	@Autowired
	private Environment environment;
	@Autowired
	private SecurityFilter securityFilter;

	private SimpleUrlAuthenticationSuccessHandler handler = new SimpleUrlAuthenticationSuccessHandler();

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		log.info(">> configure ...");
		String t = environment.getProperty("security.white.urls");
		if (!Strings.isNullOrEmpty(t)) {
			log.info(">> security.white.urls: " + t);
			http.authorizeRequests()
					// 开启授权
					.anyRequest().authenticated()
					// 允许访问
					.antMatchers(t.split(",")).permitAll()
					// 设置登陆地址
					.and().formLogin().loginPage("/login").successHandler((request, response, authentication) -> {
						ISecurityUserService securityUserService = getApplicationContext()
								.getBean(ISecurityUserService.class);
						securityUserService
								.loginSuccess(((ISecurityUserDetails) authentication.getPrincipal()).getUserId());
						handler.onAuthenticationSuccess(request, response, authentication);
					}).permitAll()
					// 设置登出地址
					.and().logout().permitAll()
					// 加入自定义过滤器
					.and().addFilterAfter(securityFilter, FilterSecurityInterceptor.class)
					// 暂停防CSRF攻击
					.csrf().disable()
					// 允许在IFRAME中嵌入展示
					.headers().frameOptions().disable();
			return;
		}
		t = environment.getProperty("security.black.urls");
		if (!Strings.isNullOrEmpty(t)) {
			log.info(">> security.black.urls: " + t);
			http.authorizeRequests()
					// 允许访问
					.anyRequest().permitAll()
					// 开启授权
					.antMatchers(t.split(",")).authenticated()
					// 设置登陆地址
					.and().formLogin().loginPage("/login").successHandler((request, response, authentication) -> {
						ISecurityUserService securityUserService = getApplicationContext()
								.getBean(ISecurityUserService.class);
						securityUserService
								.loginSuccess(((ISecurityUserDetails) authentication.getPrincipal()).getUserId());
						handler.onAuthenticationSuccess(request, response, authentication);
					}).permitAll()
					// 设置登出地址
					.and().logout().permitAll()
					// 加入自定义过滤器
					.and().addFilterAfter(securityFilter, FilterSecurityInterceptor.class)
					// 暂停防CSRF攻击
					.csrf().disable()
					// 允许在IFRAME中嵌入展示
					.headers().frameOptions().disable();
			return;
		}
		throw Exceptions.newInstance(
				"[Properties]security.white.urls and [Properties]security.black.urls should be valued at least one");
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		ISecurityUserService securityUserService = getApplicationContext().getBean(ISecurityUserService.class);
		auth.userDetailsService(securityUserService).passwordEncoder(securityUserService.getPasswordEncoder());
	}

	//////////////////////////////////////////////////
	///
	//////////////////////////////////////////////////
	@Autowired
	protected SqlExecutor sqlExecutor;

	@Bean
	public ISecurityResourceService securityResourceService() {
		String t = environment.getProperty("security.white.urls");
		if (!Strings.isNullOrEmpty(t)) {
			log.info(">> security.white.urls: " + t);
			return new BlackSecurityResourceService(sqlExecutor, t.split(","));
		}
		t = environment.getProperty("security.black.urls");
		if (!Strings.isNullOrEmpty(t)) {
			log.info(">> security.black.urls: " + t);
			return new WhiteSecurityResourceService(sqlExecutor, t.split(","));
		}
		throw Exceptions.newInstance(
				"[Properties]security.white.urls and [Properties]security.black.urls should be valued at least one");
	}

	@Bean
	public ISecurityUserService securityUserService() {
		return new SecurityUserService(sqlExecutor);
	}

}
