package com.jspring.data;

import com.jspring.Exceptions;
import com.jspring.Strings;

public class JColumnView {

	public JColumnTypes type;

	// @Basic
	public String name;
	public String title;
	// 基础信息
	public String header;
	public String width;
	public String height;
	//
	public boolean sortable = false;
	public Boolean filterable = false;
	public boolean hidden = false;// update dialog
	//
	public boolean findAllable;
	public Boolean insertable;// create dialog
	public boolean required;// required
	public boolean findOnable;// update dialog
	public Boolean updatable;// update dialog

	public JColumnView(JColumnValue column) {
		this.type = JColumnTypes.of(column.field);
		if (this.type == JColumnTypes.Unknown) {
			throw Exceptions.newIllegalArgumentException("Unsupport field type: " + column.tableData.domain.getName()
					+ "/[" + column.field.getType().getName() + "]" + column.field.getName());
		}
		//
		JColumn jc = column.field.getType().getAnnotation(JColumn.class);
		if (null == jc) {
			this.name = column.field.getName();
			this.load(column);
			return;
		}
		//
		this.name = Strings.isNullOrEmpty(jc.name()) ? column.field.getName() : jc.name();
		//
		this.header = jc.header();
		this.width = jc.width();
		this.height = jc.height();
		//
		this.filterable = jc.filterable();
		this.sortable = jc.sortable();
		this.hidden = jc.hidden();
		load(column);
	}

	private void load(JColumnValue column) {
		this.title = column.title;
		if (column.isEditable()) {
			this.insertable = column.isInsertable();
			this.updatable = column.isUpdatable();
			this.required = column.nullable ? false : true;
		} else {
			this.filterable = false;
			this.insertable = false;
			this.updatable = false;
			this.required = false;
		}
		this.findAllable = column.findAllable;
		this.findOnable = column.findOnable;
	}

}
