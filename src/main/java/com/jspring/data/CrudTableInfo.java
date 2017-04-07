package com.jspring.data;

public class CrudTableInfo {
	//
	public CrudColumnInfo[] columns;

	// 导出
	public String title;

	//
	public String width = "600px";
	public String height;
	//
	public boolean createable = true;
	public boolean createCheckNull = false;
	//
	public boolean updateable = true;
	public boolean updateCheckNull = false;
	//
	public boolean exportable = false;
}
