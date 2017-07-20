package com.jspring.security.domain;

import javax.persistence.Id;

import com.jspring.persistence.JColumn;
import com.jspring.persistence.JTable;

@JTable(name = "SECURITY_MENUS", database = "security")
public class SecurityMenu {

	public static enum Columns {
		menuId, menuname, nickName, parentId, url, orderWeight;
	}

	@Id
	public Integer menuId;
	@JColumn(unique = true)
	public String menuName;
	public String nickName;
	public String icon;
	public Integer parentId;
	public String url;// /users/**, /resource/*/detail
	public Integer orderWeight;

}
