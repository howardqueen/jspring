package com.jspring.security.service;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.jspring.security.domain.SecurityResource;
import com.jspring.security.domain.SecurityUserRepository;
import com.jspring.security.domain.SecurityRole;
import com.jspring.data.CrudRepository;

/**
 * 针对各资源获取对应的权限配置
 */
public class SecurityResourceService implements FilterInvocationSecurityMetadataSource {
	private static final Logger log = LoggerFactory.getLogger(SecurityResourceService.class);

	private final SecurityUserRepository<?> securityUserRepository;
	private final CrudRepository<SecurityResource> securityResourceRepository;

	public SecurityResourceService(SecurityUserRepository<?> securityUserRepository,
			CrudRepository<SecurityResource> securityResourceRepository) {
		this.securityUserRepository = securityUserRepository;
		this.securityResourceRepository = securityResourceRepository;
	}

	static class ResourceHolder {
		public RequestMatcher matcher;
		/**
		 * GET,POST,PUT,DELETE
		 */
		public String method;
		public Collection<ConfigAttribute> attributes = new ArrayList<ConfigAttribute>();
	}

	private Collection<ResourceHolder> resources = null;

	private void loadResources() {
		if (null != resources) {
			return;
		}
		synchronized (this) {
			if (null != resources) {
				return;
			}
			resources = new ArrayList<ResourceHolder>();
			for (SecurityResource r : securityResourceRepository.findAll(1, 1000)) {
				ResourceHolder i = new ResourceHolder();
				log.info(">> LOAD ROLES [" + r.url + ":" + r.method + "]: ");
				i.matcher = new AntPathRequestMatcher(r.url);
				i.method = r.method;
				for (SecurityRole o : securityUserRepository.findRoles(r.resourceId)) {
					log.info("  " + o.getAuthority());
					i.attributes.add(new SecurityConfig(o.getAuthority()));
				}
				resources.add(i);
			}
		}
	}

	public void resetResources() {
		resources = null;
	}

	private static final Collection<RequestMatcher> ANONYMOUS_SKIP_URLS = new ArrayList<RequestMatcher>();
	private static final Collection<ConfigAttribute> ANONYMOUS_ADMIN_AUTHS = new ArrayList<ConfigAttribute>();
	static {
		ANONYMOUS_SKIP_URLS.add(new AntPathRequestMatcher("/login*"));
		for (String u : com.jspring.security.SecurityConfig.SKIP_URLS) {
			ANONYMOUS_SKIP_URLS.add(new AntPathRequestMatcher(u));
		}
		ANONYMOUS_ADMIN_AUTHS.add(new SecurityConfig(SecurityRole.ADMIN.roleName));
	}

	public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
		loadResources();
		HttpServletRequest request = ((FilterInvocation) object).getHttpRequest();
		if (request.getMethod().equalsIgnoreCase("get")) {
			for (RequestMatcher m : ANONYMOUS_SKIP_URLS) {
				if (m.matches(request)) {
					log.debug(">> SKIP [" + request.getMethod() + ":" + request.getRequestURI() + "]");
					return null;
				}
			}
		}
		for (ResourceHolder resource : resources) {
			if (("*".equals(resource.method) || request.getMethod().equalsIgnoreCase(resource.method))
					&& resource.matcher.matches(request)) {
				log.debug(">> FOUND ROLES [" + request.getMethod() + ":" + request.getRequestURI() + "]");
				return resource.attributes;
			}
		}
		log.info(">> NOT FOUND(ADMIN ONLY) [" + request.getMethod() + ":" + request.getRequestURI() + "]");
		return ANONYMOUS_ADMIN_AUTHS;// 未查询到的配置，默认超级管理员可访问
	}

	public Collection<ConfigAttribute> getAllConfigAttributes() {
		log.info(">> GET ALL CONFIG ATTRIBUTES NULL");
		return null;
	}

	public boolean supports(Class<?> clazz) {
		return true;
	}

}
