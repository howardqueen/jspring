package com.jspring.security.domain;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "SECURITY_MENUS", catalog = "security",
		//
		schema = "`menuId` int(11) NOT NULL AUTO_INCREMENT,\n"//
				+ "`menuName` varchar(50) NOT NULL,\n"//
				+ "`nickName` varchar(50) NOT NULL,\n"//
				+ "`parentId` int(11) NOT NULL DEFAULT '0',\n"//
				+ "`url` varchar(50) NOT NULL COMMENT '/users/**,/resource/*/detail',\n"//
				+ "`orderWeight` int(11) NOT NULL DEFAULT '0',\n"//
				+ "PRIMARY KEY (`menuId`),\n"//
				+ "UNIQUE KEY `i_name` (`menuName`)\n")
public class SecurityMenu {

	public enum Columns {
		menuId, menuname, nickName, parentId, url, orderWieght;
	}

	@Id
	public Integer menuId;
	public String menuName;
	public String nickName;
	public Integer parentId;
	public String url;// /users/**, /resource/*/detail
	public Integer orderWeight;

}
