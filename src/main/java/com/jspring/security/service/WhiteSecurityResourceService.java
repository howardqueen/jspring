package com.jspring.security.service;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.jspring.data.SqlExecutor;

public class WhiteSecurityResourceService extends BaseSecurityResourceService {

	private final Collection<ConfigAttribute> ANONYMOUS_ADMIN_AUTHS = new ArrayList<ConfigAttribute>();
	private final Collection<RequestMatcher> ANONYMOUS_BLACK_URLS = new ArrayList<RequestMatcher>();

	public WhiteSecurityResourceService(SqlExecutor sqlExecutor, String[] blackUrls) {
		super(sqlExecutor);
		ANONYMOUS_ADMIN_AUTHS.add(new SecurityConfig(ADMIN.roleName));
		if (null != blackUrls && blackUrls.length > 0) {
			for (String u : blackUrls) {
				ANONYMOUS_BLACK_URLS.add(new AntPathRequestMatcher(u));
			}
		}
	}

	@Override
	protected Collection<ConfigAttribute> getDefaultAttributes() {
		return null;
	}

	@Override
	protected Collection<ConfigAttribute> getAttributes(HttpServletRequest request) {
		for (RequestMatcher m : ANONYMOUS_BLACK_URLS) {
			if (m.matches(request)) {
				for (ResourceHolder resource : getResources()) {
					if (("*".equals(resource.method) || request.getMethod().equalsIgnoreCase(resource.method))
							&& resource.matcher.matches(request)) {
						return resource.attributes;
					}
				}
				log.info(">> BLACK [" + request.getMethod() + ":" + request.getRequestURI() + "]");
				return ANONYMOUS_ADMIN_AUTHS;// 未查询到的配置，默认超级管理员可访问
			}
		}
		if (request.getMethod().equalsIgnoreCase("get")) {
			log.debug(">> WHITE [" + request.getMethod() + ":" + request.getRequestURI() + "]");
			return null;
		}
		log.info(">> BLACK [" + request.getMethod() + ":" + request.getRequestURI() + "]");
		return ANONYMOUS_ADMIN_AUTHS;// 未查询到的配置，默认超级管理员可访问
	}

}
