package com.jspring.security.service;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;

import com.jspring.security.domain.ISecurityResource;

public interface ISecurityResourceService extends FilterInvocationSecurityMetadataSource {

	Collection<? extends ISecurityResource> loadResources();
	
	void resetResources();

	Collection<? extends GrantedAuthority> loadRolesByResourceId(Integer resourceId);

}
