package com.jspring.security.service;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.FilterInvocation;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.jspring.data.SqlExecutor;
import com.jspring.data.Where;
import com.jspring.persistence.Repository;
import com.jspring.security.domain.ISecurityResource;
import com.jspring.security.domain.SecurityResource;
import com.jspring.security.domain.SecurityRole;
import com.jspring.security.domain.SecurityRoleByResource;

/**
 * 针对各资源获取对应的权限配置
 */
public abstract class BaseSecurityResourceService implements ISecurityResourceService {

	protected final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

	static class ResourceHolder {
		public RequestMatcher matcher;
		/**
		 * GET,POST,PUT,DELETE
		 */
		public String method;
		public Collection<ConfigAttribute> attributes;
	}

	public static final SecurityRole ADMIN = new SecurityRole();
	public static final Collection<SecurityRole> ADMIN_ROLES = new ArrayList<>();
	static {
		ADMIN.roleId = 0;
		ADMIN.roleName = "ROLE_ADMIN";
		ADMIN.nickName = "超级管理员";
		ADMIN_ROLES.add(ADMIN);
	}

	private final Repository<SecurityResource, Integer> resourceRepository;
	private final Repository<SecurityRoleByResource, Integer> roleByResourceRepository;

	public BaseSecurityResourceService(SqlExecutor sqlExecutor) {
		this.resourceRepository = new Repository<>(sqlExecutor, SecurityResource.class);
		this.roleByResourceRepository = new Repository<>(sqlExecutor, SecurityRoleByResource.class);
	}

	@Override
	public Collection<? extends SecurityResource> loadResources() {
		return resourceRepository.findAllList(1, 1000);
	}

	@Override
	public void resetResources() {
		log.info(">> RESET ROLES");
		_resources = null;
	}

	@Override
	public Collection<? extends GrantedAuthority> loadRolesByResourceId(Integer resourceId) {
		return roleByResourceRepository.findAllList(1, 100,
				Where.of(SecurityRoleByResource.Columns.resourceId).equalWith(resourceId));
	}

	private Collection<ResourceHolder> _resources = null;

	protected Collection<ResourceHolder> getResources() {
		if (null != _resources) {
			return _resources;
		}
		synchronized (ADMIN) {
			if (null != _resources) {
				return _resources;
			}
			_resources = new ArrayList<ResourceHolder>();
			Collection<? extends ISecurityResource> rows = loadResources();
			if (null == rows || rows.isEmpty()) {
				return _resources;
			}
			for (ISecurityResource r : rows) {
				ResourceHolder i = new ResourceHolder();
				log.info(">> LOAD ROLES [" + r.getUrl() + ":" + r.getMethod() + "]: ");
				i.matcher = new AntPathRequestMatcher(r.getUrl());
				i.method = r.getMethod();
				Collection<? extends GrantedAuthority> roles = loadRolesByResourceId(r.getResourceId());
				if (roles.isEmpty()) {
					i.attributes = getDefaultAttributes();
				} else {
					i.attributes = new ArrayList<ConfigAttribute>();
					i.attributes.add(new SecurityConfig(ADMIN.roleName));
					for (GrantedAuthority o : roles) {
						// log.debug(" " + o.getAuthority());
						i.attributes.add(new SecurityConfig(o.getAuthority()));
					}
				}
				_resources.add(i);
			}
			return _resources;
		}
	}

	protected abstract Collection<ConfigAttribute> getDefaultAttributes();

	protected abstract Collection<ConfigAttribute> getAttributes(HttpServletRequest request);

	@Override
	public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
		return getAttributes(((FilterInvocation) object).getHttpRequest());
	}

	@Override
	public Collection<ConfigAttribute> getAllConfigAttributes() {
		log.info(">> GET ALL CONFIG ATTRIBUTES");
		return getDefaultAttributes();
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return true;
	}

}
