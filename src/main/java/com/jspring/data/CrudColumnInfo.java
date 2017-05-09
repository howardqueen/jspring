package com.jspring.data;

public class CrudColumnInfo {
	// 基本信息
	public String field;
	public String fieldType;// Integer, Date, Long, Short, String
	public String title;
	public String width;
	public String height;
	// 列表
	public String header;
	public boolean sortable = false;
	// 过滤
	public boolean filterable = true;
	// 创建
	public boolean createable = true;
	public boolean required = false;
	// 更新
	public boolean updateable = true;
	public boolean readonly = false;
	// 导出
}
