package com.jspring.security.service;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.jspring.data.SqlExecutor;

public class BlackSecurityResourceService extends BaseSecurityResourceService {

	private final Collection<ConfigAttribute> ANONYMOUS_ADMIN_AUTHS = new ArrayList<ConfigAttribute>();
	private final Collection<RequestMatcher> ANONYMOUS_WHITE_URLS = new ArrayList<RequestMatcher>();

	public BlackSecurityResourceService(SqlExecutor sqlExecutor, String[] whiteUrls) {
		super(sqlExecutor);
		ANONYMOUS_ADMIN_AUTHS.add(new SecurityConfig(ADMIN.roleName));
		if (null != whiteUrls && whiteUrls.length > 0) {
			for (String u : whiteUrls) {
				ANONYMOUS_WHITE_URLS.add(new AntPathRequestMatcher(u));
			}
		}
	}

	@Override
	protected Collection<ConfigAttribute> getDefaultAttributes() {
		return ANONYMOUS_ADMIN_AUTHS;
	}

	@Override
	protected Collection<ConfigAttribute> getAttributes(HttpServletRequest request) {
		if (request.getMethod().equalsIgnoreCase("get")) {
			for (RequestMatcher m : ANONYMOUS_WHITE_URLS) {
				if (m.matches(request)) {
					log.debug(">> WHITE [" + request.getMethod() + ":" + request.getRequestURI() + "]");
					return null;
				}
			}
		}
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
