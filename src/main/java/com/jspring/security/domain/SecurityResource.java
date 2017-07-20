package com.jspring.security.domain;

import javax.persistence.Id;

import com.jspring.persistence.JTable;

@JTable(name = "SECURITY_RESOURCES", database = "security")
public class SecurityResource implements ISecurityResource {

	public static enum Columns {
		resourceId, resourceName, nickName, url, method;
	}

	@Id
	public Integer resourceId;
	public String resourceName;
	public String nickName;
	public String url;// /users/**, /resource/*/detail
	public String method;// POST/GET/PUT/DELETE

	@Override
	public Integer getResourceId() {
		return resourceId;
	}

	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public String getMethod() {
		return method;
	}

}
