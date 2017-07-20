package com.jspring.security.domain;

import org.springframework.security.core.userdetails.UserDetails;

public interface ISecurityUserDetails extends UserDetails {
	
	Integer getUserId();
	
}
