package com.jspring.security.service;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class SecurityDecisionManager implements AccessDecisionManager {
	private static final Logger log = LoggerFactory.getLogger(SecurityDecisionManager.class);

	public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes)
			throws AccessDeniedException, InsufficientAuthenticationException {
		for (GrantedAuthority o : authentication.getAuthorities()) {
			for (ConfigAttribute b : configAttributes) {
				if (o.getAuthority().equals(b.getAttribute())) {
					log.debug(">> AUTH PASSED: " + o.getAuthority());
					return;
				}
			}
		}
		log.debug(">> AUTH BLOCKED!");
		throw new AccessDeniedException("Access denied");
	}

	public boolean supports(ConfigAttribute attribute) {
		return true;
	}

	public boolean supports(Class<?> clazz) {
		return true;
	}

}
