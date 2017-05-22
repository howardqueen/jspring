package com.jspring.security.domain;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "SECURITY_USERS", catalog = "security", schema =
//
"`userId` bigint(20) NOT NULL AUTO_INCREMENT,\n"
		//
		+ "`userName` varchar(50)	NOT NULL,\n"
		//
		+ "`password` varchar(32) NOT NULL DEFAULT 'e10adc3949ba59abbe56e057f20f883e',\n"
		//
		+ "`createTime` datetime NOT NULL,\n"
		//
		+ "`lastVisit` datetime NOT NULL,\n"
		//
		+ "`nickName` varchar(20) NOT NULL,\n"
		//
		+ "`realName` varchar(20) NOT NULL,\n"
		//
		+ "`isLocked` bit(1) NOT NULL DEFAULT b'0',\n"
		//
		+ "PRIMARY KEY (`userId`)")
public class SecurityUser {

	@Id
	public Long userId;
	public String userName;
	public String password;
	public Date createTime;
	public Date lastVisit;
	public String nickName;
	public String realName;
	public Boolean isLocked;

}
