package com.jspring.security.domain;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Table;

import com.jspring.data.CrudColumn;

@Table(name = "SECURITY_USERS", catalog = "security", //
		schema = "`userId` bigint(20) NOT NULL AUTO_INCREMENT,\n"//
				+ "`userName` varchar(50)	NOT NULL,\n"//
				+ "`password` varchar(32) NOT NULL DEFAULT 'e10adc3949ba59abbe56e057f20f883e',\n"//
				+ "`createTime` datetime NOT NULL,\n"//
				+ "`lastVisit` datetime NOT NULL,\n"//
				+ "`nickName` varchar(20) NOT NULL,\n"//
				+ "`realName` varchar(20) NOT NULL,\n"//
				+ "`isLocked` bit(1) NOT NULL DEFAULT b'0',\n"//
				+ "PRIMARY KEY (`userId`)")
public class SecurityUser {
	public static enum Columns {
		userId, userName, password, createTime, lastVisit, nickName, realName, isLocked;
	}

	@Id
	@CrudColumn(title = "用户ID", header = "账户信息", filterable = false, readonly = true)
	public Long userId;
	@CrudColumn(title = "用户名", header = "账户信息", readonly = true)
	public String userName;
	@CrudColumn(title = "密码", header = "账户信息", filterable = false, readonly = true)
	public String password;
	@CrudColumn(title = "创建时间", header = "账户信息", filterable = false, readonly = true)
	public Date createTime;
	@CrudColumn(title = "最近访问", header = "账户信息", filterable = false, readonly = true)
	public Date lastVisit;
	@CrudColumn(title = "昵称", header = "账户信息", filterable = false, readonly = true)
	public String nickName;
	@CrudColumn(title = "实名", header = "账户信息", readonly = true)
	public String realName;
	@CrudColumn(title = "锁定状态", header = "账户信息", filterable = false, readonly = true)
	public Boolean isLocked;

}
