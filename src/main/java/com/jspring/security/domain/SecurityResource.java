package com.jspring.security.domain;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "SECURITY_RESOURCES", catalog = "security", //
		schema = "`resourceId` int(11) NOT NULL AUTO_INCREMENT,\n"//
				+ "`resourceName` varchar(50) NOT NULL,\n"//
				+ "`nickName` varchar(50) NOT NULL,\n"//
				+ "`url` varchar(50) NOT NULL COMMENT '/users/**,/resource/*/detail',\n"//
				+ "`method` varchar(6) NOT NULL DEFAULT 'GET' COMMENT 'GET,POST,PUT,DELETE',\n"//
				+ "PRIMARY KEY (`resourceId`),\n"//
				+ "UNIQUE KEY `i_name` (`resourceName`),\n"//
				+ "UNIQUE KEY `i_url` (`url`,`method`)")
public class SecurityResource {
	@Id
	public Integer resourceId;
	public String resourceName;
	public String nickName;
	public String url;// /users/**, /resource/*/detail
	public String method;// POST/GET/PUT/DELETE
}
