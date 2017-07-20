package com.jspring.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.jspring.Strings;

public class JTableView {

	public String title = "";
	public String width = "600px";
	public String height = "";

	public JColumnView[] columns;

	public JTableView(JTableValue table) {
		JTable t = table.domain.getAnnotation(JTable.class);
		if (null == t) {
			this.title = table.domain.getSimpleName();
			load(table);
			return;
		}
		this.title = Strings.isNullOrEmpty(t.title()) ? table.domain.getSimpleName() : t.title();
		this.width = t.width();
		this.height = t.height();
		load(table);
	}

	private void load(JTableValue table) {
		List<JColumnView> ls = new ArrayList<>();
		ls.add(table.primaryKey.createView());
		Stream.of(table.columns).forEach(a -> {
			ls.add(a.createView());
		});
		columns = ls.toArray(new JColumnView[0]);
	}

}
