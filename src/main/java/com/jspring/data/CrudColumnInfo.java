package com.jspring.data;

public class CrudColumnInfo {
	// 导出
	public String title;
	// 基本信息
	public String field;
	public String fieldType;// number, date, datetime
	// 列表
	public String header;
	public boolean sortable = false;
	// 过滤
	public String filter = "eq";
	public String width = "120px";
	public String height;
	// 创建
	public boolean createable = true;
	public boolean required = false;
	// 更新
	public boolean updateable = true;
	public boolean readonly = false;
}
