package com.jspring.security.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import com.jspring.data.JColumn;

@Table(name = "SECURITY_USERS", catalog = "security")
public class SecurityUser {
	public static enum Columns {
		userId, userName, password, createTime, lastVisit, nickName, realName, isLocked;
	}

	@Id
	@JColumn(title = "用户ID", header = "账户信息", filterable = false, updatable = true)
	public Integer userId;
	@JColumn(title = "用户名", header = "账户信息", updatable = true)
	public String userName;
	@Column(insertable = false, updatable = false)
	@JColumn(title = "密码", header = "账户信息", filterable = false, updatable = true)
	public String password;
	@JColumn(title = "创建时间", header = "账户信息", filterable = false, updatable = true)
	public Date createTime;
	@JColumn(title = "最近访问", header = "账户信息", filterable = false, updatable = true)
	public Date lastVisit;
	@JColumn(title = "昵称", header = "账户信息", filterable = false)
	public String nickName;
	@JColumn(title = "实名", header = "账户信息")
	public String realName;
	@JColumn(title = "锁定状态", header = "账户信息")
	public Boolean isLocked;

}
