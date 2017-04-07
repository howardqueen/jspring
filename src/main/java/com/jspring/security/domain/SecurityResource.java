package com.jspring.security.domain;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "cms_resources")
public class SecurityResource {
	@Id
	public Integer resourceId;
	public String url;
	public String method;// POST/GET/PUT/DELETE
}
